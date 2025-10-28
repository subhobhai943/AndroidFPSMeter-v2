# API Documentation

## Core Classes and Methods

### MainActivity.kt

The main activity that handles user interaction and service management.

#### Key Methods:

```kotlin
private fun checkOverlayPermission()
```
- **Purpose**: Checks if the app has overlay permission
- **Returns**: Updates UI based on permission status

```kotlin
private fun startFPSMeter()
```
- **Purpose**: Starts the FPS monitoring service
- **Preconditions**: Overlay permission must be granted
- **Side Effects**: Starts foreground service, updates UI

```kotlin
private fun stopFPSMeter()
```
- **Purpose**: Stops the FPS monitoring service
- **Side Effects**: Stops service, removes overlay, updates UI

### FPSMeterService.kt

Background service that manages the FPS overlay window.

#### Key Methods:

```kotlin
private fun showOverlay()
```
- **Purpose**: Creates and displays the FPS overlay window
- **Implementation**: Uses WindowManager to add overlay view
- **Parameters**: WindowManager.LayoutParams for positioning

```kotlin
private fun hideOverlay()
```
- **Purpose**: Removes the FPS overlay from screen
- **Cleanup**: Stops FPS monitoring, removes view from WindowManager

```kotlin
private fun createNotification(): Notification
```
- **Purpose**: Creates persistent notification for foreground service
- **Returns**: Notification object for service

### FPSOverlayView.kt

Custom view that implements FPS monitoring using Choreographer.

#### Key Methods:

```kotlin
fun startFPSMonitoring()
```
- **Purpose**: Begins FPS measurement
- **Implementation**: Registers Choreographer.FrameCallback

```kotlin
override fun doFrame(frameTimeNanos: Long)
```
- **Purpose**: Called on each frame render
- **Parameters**: frameTimeNanos - System time of frame render
- **Implementation**: Calculates FPS, updates display

```kotlin
private fun calculateFPS()
```
- **Purpose**: Computes FPS from frame timing data
- **Algorithm**: Uses sliding window of frame times
- **Accuracy**: Maintains 60-frame history for precision

```kotlin
private fun updateFPSDisplay()
```
- **Purpose**: Updates overlay text and colors
- **Color Coding**: Green (≥55), Yellow (30-54), Red (<30)

### PermissionHelper.kt

Utility class for managing system overlay permissions.

#### Key Methods:

```kotlin
fun hasOverlayPermission(): Boolean
```
- **Purpose**: Checks current overlay permission status
- **Returns**: true if permission granted, false otherwise
- **API Level**: Handles differences between Android versions

```kotlin
fun requestOverlayPermission()
```
- **Purpose**: Opens system settings for permission grant
- **Implementation**: Launches Settings.ACTION_MANAGE_OVERLAY_PERMISSION
- **User Flow**: Redirects to system settings page

## Technical Implementation Details

### FPS Calculation Algorithm

The app uses a sliding window approach for FPS calculation:

1. **Frame Capture**: Choreographer.FrameCallback captures each frame
2. **Time Recording**: Records nanosecond timestamps for each frame
3. **Window Management**: Maintains 60-frame history buffer
4. **FPS Calculation**: 
   ```
   fps = (frameCount - 1) * 1_000_000_000 / timeDifference
   ```
5. **Smoothing**: Averages over multiple frames for stability

### Overlay Management

The overlay system works as follows:

1. **Permission Check**: Verifies SYSTEM_ALERT_WINDOW permission
2. **Window Creation**: Creates WindowManager.LayoutParams
3. **Type Selection**: Uses TYPE_APPLICATION_OVERLAY (API 26+)
4. **Positioning**: Default top-right corner placement
5. **Display**: Adds view to WindowManager

### Memory Management

- **Frame Buffer**: Limited to 60 frames to prevent memory leaks
- **Handler Threading**: UI updates on main thread only
- **Service Lifecycle**: Proper cleanup in onDestroy()
- **Callback Management**: Removes Choreographer callbacks on stop

## Constants and Configuration

### FPSMeterService Constants

```kotlin
private const val NOTIFICATION_ID = 1001
private const val CHANNEL_ID = "fps_meter_channel"
```

### FPSOverlayView Configuration

```kotlin
private val maxFrameHistory = 60  // Frame buffer size
private val fpsFormat = DecimalFormat("0.0")  // Display precision
```

### Color Thresholds

- **Excellent (Green)**: FPS ≥ 55
- **Good (Yellow)**: FPS 30-54  
- **Poor (Red)**: FPS < 30

## Error Handling

### Common Exceptions

1. **SecurityException**: Overlay permission not granted
2. **WindowManager.BadTokenException**: Invalid window token
3. **IllegalStateException**: Service not properly initialized

### Graceful Degradation

- Service continues running even if overlay fails
- UI updates reflect current service state
- Permission requests handled gracefully

## Performance Optimizations

### CPU Usage

- Choreographer callbacks are lightweight
- FPS calculations occur every 30 frames (not every frame)
- UI updates throttled to prevent excessive redraws

### Memory Usage

- Fixed-size frame buffer prevents memory growth
- Proper cleanup of views and callbacks
- No memory leaks in service lifecycle

### Battery Impact

- Minimal: Uses existing system frame callbacks
- No continuous polling or heavy computations
- Foreground service with user visibility

## Integration Guidelines

### Adding Custom Metrics

To add additional performance metrics:

1. Extend FPSOverlayView with new TextView elements
2. Implement additional monitoring in doFrame()
3. Update layout files for new display elements

### Customizing Appearance

Modify overlay appearance by:

1. Updating colors.xml for custom color schemes
2. Changing text size in FPSOverlayView initialization
3. Adjusting overlay positioning in showOverlay()

### Platform Compatibility

- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Permission Model**: Handles runtime permissions correctly
- **API Changes**: Conditional code for different Android versions
