# Android FPS Meter

A real-time FPS (Frames Per Second) monitoring tool for Android devices that displays frame rate information as an overlay on top of other applications, including games like BGMI, PUBG Mobile, and other graphics-intensive apps.

## Features

- **Real-time FPS Monitoring**: Uses Android's Choreographer API for accurate frame rate measurement
- **System Overlay**: Displays FPS information over any running application
- **Color-coded Display**: Green (>55 FPS), Yellow (30-55 FPS), Red (<30 FPS)
- **Lightweight**: Minimal impact on device performance
- **No Root Required**: Works on non-rooted devices with proper permissions

## Technical Implementation

### Core Components

1. **FPSOverlayView**: Custom view that uses Choreographer.FrameCallback to monitor frame rendering
2. **FPSMeterService**: Foreground service that manages the overlay window
3. **PermissionHelper**: Handles system overlay permission requests
4. **MainActivity**: Simple UI for controlling the FPS meter

### How it Works

The app uses Android's `Choreographer` class to register frame callbacks and calculate real-time FPS:

```kotlin
override fun doFrame(frameTimeNanos: Long) {
    // Calculate FPS based on frame timing
    // Update overlay display
    choreographer.postFrameCallback(this)
}
```

### Permissions Required

- `SYSTEM_ALERT_WINDOW`: For displaying overlay over other apps
- `FOREGROUND_SERVICE`: For running the monitoring service
- `POST_NOTIFICATIONS`: For service notifications (Android 13+)

## Installation

1. Enable "Developer Options" on your Android device
2. Install the APK
3. Grant "Display over other apps" permission when prompted
4. Start the FPS monitoring service

## Usage

1. Open the FPS Meter app
2. Tap "Start FPS Meter" to begin monitoring
3. The FPS overlay will appear on your screen
4. Open any game or app to monitor its real-time FPS
5. Return to the app and tap "Stop FPS Meter" to disable

## Compatibility

- **Minimum SDK**: Android 5.0 (API level 21)
- **Target SDK**: Android 14 (API level 34)
- **Architecture**: ARM64, ARM32, x86_64

## Technical Notes

- Uses `Choreographer.FrameCallback` for precise frame timing
- Implements sliding window FPS calculation for accuracy
- Optimized to minimize performance impact
- Supports various screen resolutions and orientations

## Building from Source

1. Clone the repository
2. Open in Android Studio
3. Build and run on your device

## License

This project is open source and available under the MIT License.