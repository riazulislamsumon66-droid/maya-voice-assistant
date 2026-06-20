package com.myra.assistant.screenvision;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\rR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/myra/assistant/screenvision/OCRProcessor;", "", "<init>", "()V", "TAG", "", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "extractText", "", "bitmap", "Landroid/graphics/Bitmap;", "onResult", "Lkotlin/Function1;", "app_debug"})
public final class OCRProcessor {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "OCR";
    @org.jetbrains.annotations.NotNull()
    private static final com.google.mlkit.vision.text.TextRecognizer recognizer = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.screenvision.OCRProcessor INSTANCE = null;
    
    private OCRProcessor() {
        super();
    }
    
    public final void extractText(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onResult) {
    }
}