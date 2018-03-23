package io.embs;

import brave.Tracer;
import brave.Tracing;

import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        OkHttpSender sender;
        AsyncReporter spanReporter;
        Tracing tracing;
        Tracer tracer;

        // Configure a reporter, which controls how often spans are sent
        //   (the dependency is io.zipkin.reporter2:zipkin-sender-okhttp3)
        sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
        spanReporter = AsyncReporter.create(sender);

        // Create a tracing component with the service name you want to see in Zipkin.
        tracing = Tracing.newBuilder()
                         .localServiceName("my-service")
                         .spanReporter(spanReporter)
                         .build();

        // Tracing exposes objects you might need, most importantly the tracer
        tracer = tracing.tracer();

        tracer.nextSpan().name("encode").start().finish();
        

        // Failing to close resources can result in dropped spans! When tracing is no
        // longer needed, close the components you made in reverse order. This might be
        // a shutdown hook for some users.
        tracing.close();
        spanReporter.close();
        sender.close();
    }
}
