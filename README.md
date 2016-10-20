# CameraCompat

## Usage

### SetUp
```
<FrameLayout
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```
CameraCompatFragment cameraFragment = CameraCompatFragmentFactory.getInstance();
getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, (Fragment) cameraFragment)
        .commitAllowingStateLoss();
```

### Take Picture
```
cameraFragment.setCallbackListener(new CameraCompatCallback() {
    @Override
    public void takePicture(Bitmap bitmap) {
        // callback when picture taken
    }

    @Override
    public void requestCameraPermission() {
      // request permission for API 21-
    }

    @Override
    public void onFocusStateChanged(FocusState state) {

    }

    @Override
    public void showPermissionError() {
      // permission error for API 21-
    }
});

int maxSize = 800;
cameraFragment.takePicture(maxSize, Bitmap.Config.ARGB_8888);
```
