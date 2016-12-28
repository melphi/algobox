package io.algobox.api.module.strategy.domain.dto;

import io.algobox.api.module.strategy.domain.StrategyInfo;
import io.algobox.strategy.StrategyStatus;

public final class StrategyInfoDto implements StrategyInfo {
  private StrategyStatus status;

  private long receivedTicks;

  public StrategyInfoDto(StrategyStatus status, long receivedTicks) {
    this.status = status;
    this.receivedTicks = receivedTicks;
  }

  @Override
  public StrategyStatus getStatus() {
    return status;
  }

  @Override
  public long getReceivedTicks() {
    return receivedTicks;
  }
}
