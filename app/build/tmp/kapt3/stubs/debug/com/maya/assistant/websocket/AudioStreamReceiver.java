package com.myra.assistant.websocket;

/**
 * WebSocket-level audio stream receiver callback holder
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0002\u0010\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001b\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0004R\u001d\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\f"}, d2 = {"Lcom/myra/assistant/websocket/AudioStreamReceiver;", "", "onData", "Lkotlin/Function1;", "", "", "<init>", "(Lkotlin/jvm/functions/Function1;)V", "getOnData", "()Lkotlin/jvm/functions/Function1;", "receive", "data", "app_debug"})
public final class AudioStreamReceiver {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<byte[], kotlin.Unit> onData = null;
    
    public AudioStreamReceiver(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super byte[], kotlin.Unit> onData) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<byte[], kotlin.Unit> getOnData() {
        return null;
    }
    
    public final void receive(@org.jetbrains.annotations.NotNull()
    byte[] data) {
    }
}