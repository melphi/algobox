from logging import logging
from pyspark import SparkContext, StorageLevel
from sys import argv
from . configuration import Configuration
from . prices_dao import PricesDao


log = logging.getLogger('aggregation')


# TODO: Read https://www.appsflyer.com/blog/the-bleeding-edge-spark-parquet-and-s3/
# TODO: Specify schema with http://spark.apache.org/docs/latest/sql-programming-guide.html#programmatically-specifying-the-schema
def _get_prices_dao(parameters):
    connection_url = parameters['mongo.connectionUrl']
    database = parameters['mongo.database']
    credentials = (parameters['mongo.user'], parameters['mongo.pwd'])
    log.info('Connecting to Mongo [%s], db [%s].', (connection_url, database))
    return PricesDao(connection_url=connection_url, database=database,
                     credentials=credentials)


def _run_task(configuration):
    parameters = configuration.get_parameters()
    prices_dao = _get_prices_dao(parameters)
    prices = prices_dao.get_prices_stage()

    log.info('Running Spark aggregation task.')
    sc = SparkContext("local", "Algobox aggregator")
    prices_rdd = sc.parallelize(prices)\
        .sort()\
        .persist(StorageLevel.MEMORY_AND_DISK)

    # Save prices in parquet format
    prices_rdd.

    # Mark prices as imported or duplicated

    # Remove imported prices

if __name__ == '__main__':
    assert len(argv) > 1, 'Missing key/value store base url.'
    _run_task(Configuration(argv[1]))
