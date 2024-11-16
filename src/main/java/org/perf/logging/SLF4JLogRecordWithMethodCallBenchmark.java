package org.perf.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.OutputStreamAppender;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import static ch.qos.logback.classic.Level.convertAnSLF4JLevel;
import static java.lang.String.format;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@State(Scope.Benchmark)
public class SLF4JLogRecordWithMethodCallBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(SLF4JLogRecordWithMethodCallBenchmark.class);

    @Param({"INFO", "DEBUG"})
    private static String theLevel;
    @Param({"P1"})
    private static String aString;
    @Param({"42"})
    private static int anInt;
    @Param({"00.42f"})
    private static float aFloat;
    @Param({"true"})
    private static boolean aBoolean;
    @Param({"!"})
    private static char aChar;

    private Level logLevel;

    @Setup
    public void setUp(final Blackhole blackhole) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream() {
            @Override
            public void write(int b) {
                blackhole.consume(b);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                blackhole.consume(b);
            }

            @Override
            public void write(byte[] b) {
                blackhole.consume(b);
            }
        };

        var rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders();

        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LOGGER.getName());
        LoggerContext loggerContext = logger.getLoggerContext();
        logger.detachAndStopAllAppenders();
        logger.setAdditive(false);

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.start();

        var appender = new OutputStreamAppender();
        appender.setOutputStream(bos);
        appender.setEncoder(patternLayoutEncoder);
        appender.setContext(loggerContext);
        appender.start();

        logger.addAppender(appender);

        logLevel = "INFO".equals(theLevel) ? Level.INFO : Level.DEBUG;
        logger.setLevel(convertAnSLF4JLevel(logLevel));
    }

    @Benchmark
    public void l01_string_format() {
        LOGGER.debug(format("Result [%s], [%s], [%s], [%s], [%s], [%s]", aString, ++anInt, aBoolean, aFloat++, aChar, computeLongValue(anInt)));
    }

    @Benchmark
    public void l02_lambda_heap() {
        LOGGER.atDebug().log(() -> ("Result [" + aString + "], [" + (++anInt) + "], [" + aBoolean + "], [" + aFloat++ + "], [" + aChar + "], [" + computeLongValue(anInt) + "]"));
    }

    @Benchmark
    public void l03_lambda_local() {
        String localString = aString;
        int localInt = ++anInt;
        boolean localBoolean = aBoolean;
        float localFloat = aFloat++;
        char localChar = aChar;
        long localLong = computeLongValue(anInt);
        LOGGER.atDebug().log(() -> ("Result [" + localString + "], [" + localInt + "], [" + localBoolean + "], [" + localFloat + "], [" + localChar + "], [" + localLong + "]"));
    }

    @Benchmark
    public void l04_unguarded_parametrized() {
        LOGGER.debug("Result [{}], [{}], [{}], [{}], [{}], [{}]", aString, ++anInt, aBoolean, aFloat++, aChar, computeLongValue(anInt));
    }

    @Benchmark
    public void l05_guarded_parametrized() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Result [{}], [{}], [{}], [{}], [{}], [{}]", aString, ++anInt, aBoolean, aFloat++, aChar, computeLongValue(anInt));
        }
    }

    @Benchmark
    public void l06_unguarded_unparametrized() {
        LOGGER.debug("Result [" + aString + "], [" + (++anInt) + "], [" + aBoolean + "], [" + (aFloat++) + "], [" + aChar + "], [" + computeLongValue(anInt) + "]");
    }

    @Benchmark
    public void l08_guarded_unparametrized() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Result [" + aString + "], [" + (++anInt) + "], [" + aBoolean + "], [" + (aFloat++) + "], [" + aChar + "], [" + computeLongValue(anInt) + "]");
        }
    }

    private static long computeLongValue(int iteration) {
        long computedValue = 0;

        for (int i = 0; i < iteration * 100; i++) {
            computedValue += i;
        }

        return computedValue;
    }

}