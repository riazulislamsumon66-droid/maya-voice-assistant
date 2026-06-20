package com.myra.assistant.apps;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\n\u001a\u00020\u000bJ\u0018\u0010\f\u001a\u0004\u0018\u00010\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u0005J\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u0005J\u0006\u0010\u0010\u001a\u00020\u0011R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/myra/assistant/apps/InstalledAppsManager;", "", "<init>", "()V", "TAG", "", "cachedApps", "", "Lcom/myra/assistant/models/AppModel;", "getAllApps", "context", "Landroid/content/Context;", "findApp", "query", "getLaunchIntent", "Landroid/content/Intent;", "invalidateCache", "", "app_debug"})
public final class InstalledAppsManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "APPS";
    @org.jetbrains.annotations.Nullable()
    private static java.util.List<com.myra.assistant.models.AppModel> cachedApps;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.apps.InstalledAppsManager INSTANCE = null;
    
    private InstalledAppsManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.myra.assistant.models.AppModel> getAllApps(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.myra.assistant.models.AppModel findApp(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.content.Intent getLaunchIntent(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    public final void invalidateCache() {
    }
}