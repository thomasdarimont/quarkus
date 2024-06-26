package io.quarkus.grpc.server.services;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.arc.Arc;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.impl.EventLoopContext;
import io.vertx.core.impl.WorkerContext;

public class AssertHelper {

    public static void assertThatTheRequestScopeIsActive() {
        assertThat(Arc.container().requestContext().isActive()).isTrue();
    }

    public static void assertRunOnEventLoop() {
        assertThat(Vertx.currentContext()).isNotNull();
        assertThat(Vertx.currentContext().isEventLoopContext());
        assertThat(Thread.currentThread().getName()).contains("eventloop");
    }

    public static Context assertRunOnDuplicatedContext() {
        assertThat(Vertx.currentContext()).isNotNull();
        assertThat(isRootContext(Vertx.currentContext())).isFalse();
        return Vertx.currentContext();
    }

    private static boolean isRootContext(Context context) {
        return context instanceof EventLoopContext || context instanceof WorkerContext;
    }

    public static void assertRunOnWorker() {
        assertThat(Vertx.currentContext()).isNotNull();
        assertThat(Thread.currentThread().getName()).contains("executor");
    }

}
