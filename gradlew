#!/bin/sh

# Gradle wrapper script for Android
# Downloads and runs the correct Gradle version

APP_BASE_NAME=$(basename "$0")
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Find Java
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check for Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    # Try common Android SDK locations on Android
    for path in \
        /storage/emulated/0/AndroidIDEProjects/maya-voice-assistant \
        /data/data/com.tom.rv2ide/files/home/.androidide \
        /sdcard/AndroidIDEProjects/maya-voice-assistant; do
        if [ -d "$path" ]; then
            export ANDROID_HOME="$path"
            break
        fi
    done
fi

# Set SDK dir in local.properties if not set
if [ ! -f "$APP_HOME/local.properties" ]; then
    if [ -n "$ANDROID_HOME" ]; then
        echo "sdk.dir=$ANDROID_HOME" > "$APP_HOME/local.properties"
    elif [ -n "$ANDROID_SDK_ROOT" ]; then
        echo "sdk.dir=$ANDROID_SDK_ROOT" > "$APP_HOME/local.properties"
    fi
fi

# Run Gradle
exec "$JAVACMD" -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
