package io.algobox.api.module.price.rest;

import io.algobox.api.module.price.domain.mdb.PriceTickMdb;
import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Api(tags = "prices")
@Path("/prices")
@Singleton
public final class PriceController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PriceController.class);
  private static final String AVRO_PRICE_TICK_SCHEMA = "pricetick.avsc";

  private final PriceService priceService;
  private final Schema priceTickSchemaAvro;


  @Inject
  public PriceController(PriceService historicalPriceService) {
    this.priceService = historicalPriceService;
    try {
      InputStream priceTickSchema =
          this.getClass().getClassLoader().getResourceAsStream(AVRO_PRICE_TICK_SCHEMA);
      this.priceTickSchemaAvro = new Schema.Parser().parse(priceTickSchema);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(
          "Error while parsing Avro schema [%s]: [%s].", AVRO_PRICE_TICK_SCHEMA, e.getMessage()));
    }
  }

  @ApiOperation(
      value = "Returns the prices ticks.", responseContainer = "List", response = PriceTick.class)
  @GET
  @Path("/{instrumentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public void getPriceTicks(@PathParam("instrumentId") String instrumentId,
      @ApiParam(value = "From timestamp in milliseconds UTC", required = true)
      @QueryParam("fromTimestamp") long fromTimestamp,
      @ApiParam(value = "To timestamp in milliseconds UTC", required = true)
      @QueryParam("toTimestamp") long toTimestamp,
      @ApiParam(hidden = true) @Suspended AsyncResponse response) throws Exception {
    new Thread(() -> {
      try {
        final Iterable<PriceTick> result = priceService.getPriceTicks(
            instrumentId, fromTimestamp, toTimestamp);
        response.resume(result);
      } catch (Exception e) {
        response.resume(e);
      }
    }).start();
  }

  @ApiOperation(value = "Returns the prices ticks in Avro format.")
  @GET
  @Path("/{instrumentId}/avro")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public void getPriceTicksAvro(@PathParam("instrumentId") String instrumentId,
      @ApiParam(value = "From timestamp in milliseconds UTC", required = true)
      @QueryParam("fromTimestamp") long fromTimestamp,
      @ApiParam(value = "To timestamp in milliseconds UTC", required = true)
      @QueryParam("toTimestamp") long toTimestamp,
      @ApiParam(hidden = true) @Suspended AsyncResponse response) throws Exception {
    new Thread(() -> {
      try {
        final Iterable<PriceTick> priceTicks = priceService.getPriceTicks(
            instrumentId, fromTimestamp, toTimestamp);
        StreamingOutput streamingOutput = new StreamingOutput() {
          @Override
          public void write(OutputStream output) throws IOException, WebApplicationException {
            try {
              writePriceTicksAvro(priceTicks, output);
            } catch (Exception e) {
              LOGGER.error(e.getMessage(), e);
            }
          }
        };
        response.resume(streamingOutput);
      } catch (Exception e) {
        response.resume(e);
      }
    }).start();
  }

  private void writePriceTicksAvro(Iterable<PriceTick> priceTicks, OutputStream outputStream)
      throws IOException {
    DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(priceTickSchemaAvro);
    DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
    dataFileWriter.setCodec(CodecFactory.snappyCodec());
    dataFileWriter.create(priceTickSchemaAvro, outputStream);
    for (PriceTick priceTick: priceTicks) {
      dataFileWriter.append(createGenericRecord(priceTick, priceTickSchemaAvro));
    }
    dataFileWriter.close();
    outputStream.close();
  }

  private GenericRecord createGenericRecord(PriceTick priceTick, Schema schema) {
    GenericRecord record = new GenericData.Record(schema);
    record.put(PriceTickMdb.FIELD_INSTRUMENT, priceTick.getInstrument());
    record.put(PriceTickMdb.FIELD_TIME, priceTick.getTime());
    record.put(PriceTickMdb.FIELD_ASK, priceTick.getAsk());
    record.put(PriceTickMdb.FIELD_BID, priceTick.getBid());
    return record;
  }
}
