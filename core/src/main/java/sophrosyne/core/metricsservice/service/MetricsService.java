package sophrosyne.core.metricsservice.service;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.resources.Resource;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MetricsService {

  private static final String SERVICE_NAME = "SOPHROSYNE";
  private final Logger logger = LogManager.getLogger(getClass());

  @Value("${telemetry.prometheus.standard_job.host}")
  private String PROMETHEUS_STANDARD_JOB_HOST;

  @Value("${telemetry.prometheus.standard_job.port}")
  private int PROMETHEUS_STANDARD_JOB_PORT;

  @Value("${telemetry.prometheus.high_loads_job.host}")
  private String PROMETHEUS_HIGH_LOADS_JOB_HOST;

  @Value("${telemetry.prometheus.high_loads_job.port}")
  private int PROMETHEUS_HIGH_LOADS_JOB_PORT;

  private OpenTelemetry openTelemetry_standard_job;

  private OpenTelemetry openTelemetry_high_loads_job;

  @PostConstruct
  private void initPrometheusOpenTelemetry() {
    try {
      Resource resource =
          Resource.getDefault()
              .merge(Resource.builder().put(SERVICE_NAME, "SophrosynePrometheusExporter").build());

      OpenTelemetrySdk openTelemetrySdk =
          OpenTelemetrySdk.builder()
              .setMeterProvider(
                  SdkMeterProvider.builder()
                      .setResource(resource)
                      .registerMetricReader(
                          PrometheusHttpServer.builder()
                              .setHost(PROMETHEUS_STANDARD_JOB_HOST)
                              .setPort(PROMETHEUS_STANDARD_JOB_PORT)
                              .build())
                      .build())
              .build();

      Runtime.getRuntime().addShutdownHook(new Thread(openTelemetrySdk::close));

      logger.info(
          "Prometheus standard metrics server running on {}:{}/metrics",
          PROMETHEUS_STANDARD_JOB_HOST,
          PROMETHEUS_STANDARD_JOB_PORT);

      this.openTelemetry_standard_job = openTelemetrySdk;
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @PostConstruct
  private void initPrometheusOpenTelemetryHighLoads() {
    try {
      Resource resource =
          Resource.getDefault()
              .merge(
                  Resource.builder()
                      .put(SERVICE_NAME, "SophrosynePrometheusExporterHighLoads")
                      .build());

      OpenTelemetrySdk openTelemetrySdk =
          OpenTelemetrySdk.builder()
              .setMeterProvider(
                  SdkMeterProvider.builder()
                      .setResource(resource)
                      .registerMetricReader(
                          PrometheusHttpServer.builder()
                              .setHost(PROMETHEUS_HIGH_LOADS_JOB_HOST)
                              .setPort(PROMETHEUS_HIGH_LOADS_JOB_PORT)
                              .build())
                      .build())
              .build();
      Runtime.getRuntime().addShutdownHook(new Thread(openTelemetrySdk::close));

      logger.info(
          "Prometheus high loads metrics server running on {}:{}/metrics",
          PROMETHEUS_HIGH_LOADS_JOB_HOST,
          PROMETHEUS_HIGH_LOADS_JOB_PORT);

      this.openTelemetry_high_loads_job = openTelemetrySdk;
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public Meter getSophrosyneStandardJobMeter() {
    return openTelemetry_standard_job.getMeter("sophrosyne_standard_meter");
  }

  public Meter getSophrosyneHighLoadsJobMeter() {
    return openTelemetry_high_loads_job.getMeter("sophrosyne_high_loads_meter");
  }
}
