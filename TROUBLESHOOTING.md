# Troubleshooting Guide

## Common Issues and Solutions

### 1. App Won't Start

#### Symptoms:
- App crashes immediately after launch
- Black screen on startup
- "App has stopped" error message

#### Solutions:
1. **Check Android Version**
   - Ensure device runs Android 5.0+ (API level 21)
   - Older versions are not supported

2. **Clear App Data**
   - Go to Settings > Apps > FPS Meter
   - Tap "Storage" > "Clear Data"
   - Restart the app

3. **Reinstall APK**
   - Uninstall current version
   - Download fresh APK from releases
   - Install and try again

### 2. Permission Issues

#### Symptoms:
- "Overlay permission required" message persists
- App redirects to settings but permission doesn't work
- Button shows "Grant Permission" continuously

#### Solutions:
1. **Manual Permission Grant**
   - Go to Settings > Apps > Special Access
   - Find "Display over other apps"
   - Locate "FPS Meter" and enable it

2. **MIUI/ColorOS Specific**
   - MIUI: Security > Permissions > Display pop-up window
   - ColorOS: Settings > Additional Settings > Permission privacy
   - Enable overlay permission for FPS Meter

3. **Samsung Devices**
   - Settings > Apps > FPS Meter > Permissions
   - Enable "Appear on top" permission

### 3. Overlay Not Showing

#### Symptoms:
- Service starts but no overlay appears
- Notification shows but no FPS counter visible
- App claims to be running but nothing on screen

#### Solutions:
1. **Check Background Apps**
   - Some devices kill background services
   - Go to Settings > Battery > Battery Optimization
   - Find "FPS Meter" and set to "Don't optimize"

2. **Gaming Mode Interference**
   - Disable gaming mode temporarily
   - Some gaming modes hide overlays
   - Test in normal mode first

3. **Display Settings**
   - Check if device has "Hide overlay" in developer options
   - Ensure display scale is not causing positioning issues
   - Try restarting the device

### 4. Inaccurate FPS Readings

#### Symptoms:
- FPS counter shows unrealistic numbers
- Counter doesn't change during gameplay
- Numbers seem stuck or frozen

#### Understanding FPS Measurement:
- **System UI FPS**: App measures system-level frame rate
- **Game-Specific**: Some games cap their own frame rate
- **Hardware Limits**: Device capabilities affect readings

#### Solutions:
1. **Verify with Known Benchmarks**
   - Test with simple apps first (Settings, Calculator)
   - Compare with built-in game tools if available
   - Use multiple FPS measurement apps for comparison

2. **Check Game Settings**
   - Disable game-specific FPS limits
   - Adjust graphics quality settings
   - Ensure game is not in battery saving mode

### 5. Battery Drain

#### Symptoms:
- Excessive battery consumption
- Device heating up
- Faster battery depletion than normal

#### Solutions:
1. **Normal Behavior**
   - Minimal impact expected (<1% battery per hour)
   - Foreground service notification normal
   - No significant CPU usage

2. **If Excessive Drain Occurs**
   - Restart the FPS monitoring service
   - Check for conflicting apps
   - Update to latest version if available

### 6. Service Stops Automatically

#### Symptoms:
- FPS counter disappears after some time
- Service notification vanishes
- Need to restart app frequently

#### Solutions:
1. **Battery Optimization**
   - Disable battery optimization for FPS Meter
   - Add to "Never sleeping apps" list
   - Disable adaptive battery for this app

2. **Manufacturer-Specific**
   - **Xiaomi**: Security > Permissions > Autostart > Enable
   - **Huawei**: Phone Manager > Protected Apps > Enable
   - **OnePlus**: Settings > Battery > Battery Optimization > Disable
   - **Samsung**: Device Care > Battery > App Power Mgmt > Disable

### 7. App Compatibility Issues

#### Device-Specific Problems:

1. **Chinese ROMs (MIUI, EMUI, ColorOS)**
   - May have additional overlay restrictions
   - Check manufacturer-specific permission settings
   - Some may require "Trust this app" setting

2. **Android Go Devices**
   - Limited RAM may cause service termination
   - Close other apps before starting FPS monitoring
   - Consider using lightweight mode if available

3. **Foldable Devices**
   - Overlay position may be incorrect on fold/unfold
   - Restart service after changing screen configuration
   - Position may need manual adjustment

### 8. Performance Issues

#### Symptoms:
- Device lag while FPS counter is active
- Games running slower than usual
- System stuttering

#### Solutions:
1. **Normal Impact**
   - FPS monitoring has minimal performance impact
   - Less than 1-2% CPU usage typical
   - No significant memory consumption

2. **If Performance Issues Occur**
   - Close other background apps
   - Reduce overlay update frequency if possible
   - Check for device thermal throttling

## Advanced Troubleshooting

### Debug Mode Information

For developers or advanced users:

1. **Check Logcat Output**
   ```bash
   adb logcat | grep -i "fpsmeter"
   ```

2. **Common Error Messages**
   - `SecurityException`: Permission issue
   - `BadTokenException`: Window manager problem
   - `IllegalStateException`: Service lifecycle issue

### Manual Testing Steps

1. **Permission Verification**
   ```kotlin
   Settings.canDrawOverlays(context)
   ```
   Should return `true`

2. **Service Status Check**
   - Check if service appears in running services
   - Verify notification is persistent
   - Confirm overlay view is added to WindowManager

### Recovery Procedures

#### Complete Reset:
1. Stop FPS monitoring
2. Force close app
3. Clear app data
4. Restart device
5. Reinstall app
6. Grant permissions fresh

#### Factory Reset Scenario:
- Re-download APK
- Fresh permission grants required
- Previous settings will be lost

## Getting Help

### Before Reporting Issues:

1. **Check System Information**
   - Android version
   - Device model and manufacturer
   - Available RAM and storage
   - Other overlay apps installed

2. **Reproduce the Issue**
   - Steps to reproduce problem
   - When it first occurred
   - Any error messages seen

3. **Try Basic Solutions**
   - Restart app
   - Restart device
   - Clear app data
   - Reinstall APK

### Reporting Bugs:

Create a GitHub issue with:
- Device information
- Android version
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Logcat output if available

### Community Support:

- Check existing GitHub issues
- Search for similar problems
- Provide detailed information when asking for help
- Test suggested solutions and report back
