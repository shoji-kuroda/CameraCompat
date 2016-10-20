# CameraCompat

## Usage

1. copy `com.moneyforward.cameracompat/build/outputs/aar/com.moneyforward.cameracompat-vX.X.X.aar`
1. paste to libs directory in your app

```gradle
compile(name: 'com.moneyforward.cameracompat-vX.X.X', ext: 'aar')
```


### SetUp
```xml
<FrameLayout
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```java
CameraCompatFragment cameraFragment = CameraCompatFragmentFactory.getInstance();
getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, (Fragment) cameraFragment)
        .commitAllowingStateLoss();
```

### Take Picture

```java
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

## Turn on Flashlight

```java
cameraFragment.setFlash(true); // ON

cameraFragment.setFlash(false); // OFF
```
