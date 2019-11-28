package com.naveensaini.dwintest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;


public class ZOTestingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_activity_testing);


        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        bitmapArrayList.add(BitmapFactory.decodeResource(getResources(), R.drawable.got));
        bitmapArrayList.add(BitmapFactory.decodeResource(getResources(), R.drawable.app_background_gradient));
        bitmapArrayList.add(BitmapFactory.decodeResource(getResources(), R.drawable.got));
        bitmapArrayList.add(BitmapFactory.decodeResource(getResources(), R.drawable.app_background_gradient));

        RecyclerView zRecyclerView = findViewById(R.id.zRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.HORIZONTAL, false);

        SnapHelper dSnapHelper = new PagerSnapHelper();
        dSnapHelper.attachToRecyclerView(zRecyclerView);

        zRecyclerView.setLayoutManager(layoutManager);
        DCImagesRecyclerAdapter adapter = new DCImagesRecyclerAdapter
                (this, bitmapArrayList);
        zRecyclerView.setAdapter(adapter);
    }
}

/*getSupportActionBar().hide();
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
		if (null == savedInstanceState) {
			getFragmentManager().beginTransaction()
					.replace(R.id.zActivityContainer, ZOCameraFragment.newInstance())
					.commit();

		}

		ImageView zImageView = findViewById(R.id.zImageView);
		MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
		String AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AVLog/Recordings/"
				+ DAEntryViewActivity.dEntryReferenceNumber + "VideoRecording.3gp";

		mediaMetadataRetriever.setDataSource(AudioSavePathInDevice);

		Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
		Log.i("zBitmap", bitmap.toString());
		zImageView.setImageBitmap(bitmap);





///////////////////////////////
////////////////////////////////
//////////////////////////////////
 */




	/*
	private static final String TAG = "AndroidCameraApi";
	private Button takePictureButton;
	private TextureView textureView;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}

	private String cameraId;
	protected CameraDevice cameraDevice;
	protected CameraCaptureSession cameraCaptureSessions;
	protected CaptureRequest captureRequest;
	protected CaptureRequest.Builder captureRequestBuilder;
	private Size imageDimension;
	private ImageReader imageReader;
	private File file;
	private static final int REQUEST_CAMERA_PERMISSION = 200;
	private boolean mFlashSupported;
	private Handler mBackgroundHandler;
	private HandlerThread mBackgroundThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_activity_testing);


		textureView = (TextureView) findViewById(R.id.zTextureView);
		assert textureView != null;
		textureView.setSurfaceTextureListener(textureListener);
		takePictureButton = (Button) findViewById(R.id.zButtonRecord);
		assert takePictureButton != null;
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePicture();
			}
		});
	}

	TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			//open your camera here
			openCamera();
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
			// Transform you image captured size according to the surface width and height
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			return false;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		}
	};


	private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
		@Override
		public void onOpened(CameraDevice camera) {
			//This is called when the camera is open
			Log.e(TAG, "onOpened");
			cameraDevice = camera;
			createCameraPreview();
		}

		@Override
		public void onDisconnected(CameraDevice camera) {
			cameraDevice.close();
		}

		@Override
		public void onError(CameraDevice camera, int error) {
			cameraDevice.close();
			cameraDevice = null;
		}
	};
	final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
		@Override
		public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
			super.onCaptureCompleted(session, request, result);
			Toast.makeText(ZOTestingActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
			createCameraPreview();
		}
	};

	protected void startBackgroundThread() {
		mBackgroundThread = new HandlerThread("Camera Background");
		mBackgroundThread.start();
		mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
	}

	protected void stopBackgroundThread() {
		mBackgroundThread.quitSafely();
		try {
			mBackgroundThread.join();
			mBackgroundThread = null;
			mBackgroundHandler = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void takePicture() {
		if (null == cameraDevice) {
			Log.e(TAG, "cameraDevice is null");
			return;
		}
		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
			Size[] jpegSizes = null;
			if (characteristics != null) {
				jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
			}
			int width = 640;
			int height = 480;
			if (jpegSizes != null && 0 < jpegSizes.length) {
				width = jpegSizes[0].getWidth();
				height = jpegSizes[0].getHeight();
			}
			Log.i(TAG, String.valueOf(width)+","+String.valueOf(height));

			ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
			List<Surface> outputSurfaces = new ArrayList<Surface>(2);
			outputSurfaces.add(reader.getSurface());
			outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
			final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			captureBuilder.addTarget(reader.getSurface());
			captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
			// Orientation
			int rotation = getWindowManager().getDefaultDisplay().getRotation();
			captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
			final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
			ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
				@Override
				public void onImageAvailable(ImageReader reader) {
					Image image = null;
					try {
						image = reader.acquireLatestImage();
						ByteBuffer buffer = image.getPlanes()[0].getBuffer();
						byte[] bytes = new byte[buffer.capacity()];
						buffer.get(bytes);
						save(bytes);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (image != null) {
							image.close();
						}
					}
				}

				private void save(byte[] bytes) throws IOException {
					OutputStream output = null;
					try {
						output = new FileOutputStream(file);
						output.write(bytes);
					} finally {
						if (null != output) {
							output.close();
						}
					}
				}
			};
			reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
			final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
				@Override
				public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
					super.onCaptureCompleted(session, request, result);
					Log.i(TAG, "onCapture completed");
					Toast.makeText(ZOTestingActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
					createCameraPreview();
				}
			};
			cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
				@Override
				public void onConfigured(CameraCaptureSession session) {
					Log.i(TAG, String.valueOf(session == null)+ "Session");
					try {
						session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
					} catch (CameraAccessException e) {
						e.printStackTrace();

					}
				}

				@Override
				public void onConfigureFailed(CameraCaptureSession session) {
				}
			}, mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	protected void createCameraPreview() {
		try {
			SurfaceTexture texture = textureView.getSurfaceTexture();
			assert texture != null;
			texture.setDefaultBufferSize(600, 800);
			Log.i(TAG, "Default Buffer Size"+imageDimension.getWidth()+","+imageDimension.getHeight());
			Surface surface = new Surface(texture);
			captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			captureRequestBuilder.addTarget(surface);
			cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
				@Override
				public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
					//The camera is already closed
					if (null == cameraDevice) {
						return;
					}
					// When the session is ready, we start displaying the preview.
					cameraCaptureSessions = cameraCaptureSession;
					updatePreview();


					Log.i(TAG, "capture session configured");
				}

				@Override
				public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
					Toast.makeText(ZOTestingActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
				}
			}, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void openCamera() {
		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		Log.e(TAG, "is camera open");
		try {
			cameraId = manager.getCameraIdList()[0];
			CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
			StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			assert map != null;
			imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

			// Add permission for camera and let user grant the permission
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(ZOTestingActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
				return;
			}
			manager.openCamera(cameraId, stateCallback, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		Log.e(TAG, "openCamera X");
	}

	protected void updatePreview() {
		if (null == cameraDevice) {
			Log.e(TAG, "updatePreview error, return");
		}
		captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
		try {
			cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void closeCamera() {
		if (null != cameraDevice) {
			cameraDevice.close();
			cameraDevice = null;
		}
		if (null != imageReader) {
			imageReader.close();
			imageReader = null;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_CAMERA_PERMISSION) {
			if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
				// close the app
				Toast.makeText(ZOTestingActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
		startBackgroundThread();
		if (textureView.isAvailable()) {
			openCamera();
		} else {
			textureView.setSurfaceTextureListener(textureListener);
		}
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "onPause");
		//closeCamera();
		stopBackgroundThread();
		super.onPause();
	}


















/*
	CameraDevice zCameraDevice;
	TextureView zTextureView;
	CaptureRequest.Builder zCaptureRequestBuilder;


	private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			openCamera();
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			return false;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}
	};


	private CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
		@Override
		public void onOpened(@NonNull CameraDevice camera) {
			zCameraDevice = camera;
			startCameraPreview();
		}

		@Override
		public void onDisconnected(@NonNull CameraDevice camera) {

		}

		@Override
		public void onError(@NonNull CameraDevice camera, int error) {

		}
	};

	private void startCameraPreview() {
		try {
			SurfaceTexture zSurfaceTexture = zTextureView.getSurfaceTexture();
			zSurfaceTexture.setDefaultBufferSize(previewSize.getWidth);
			zCaptureRequestBuilder = zCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			Surface zSurface = new Surface(zSurfaceTexture);
			zCaptureRequestBuilder.addTarget(zSurface);

			zCameraDevice.createCaptureSession(Arrays.asList(zSurface), new CameraCaptureSession.StateCallback() {
				@Override
				public void onConfigured(@NonNull CameraCaptureSession session) {
					if (zCameraDevice == null || zTextureView.isAvailable() || previewSize == null) {
						return;
					}
					//camera
				}

				@Override
				public void onConfigureFailed(@NonNull CameraCaptureSession session) {

				}
			});


		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_activity_testing);


		zTextureView = findViewById(R.id.zTextureView);
		zTextureView.setSurfaceTextureListener(surfaceTextureListener);

	}

	private void openCamera() {
		CameraManager zCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

		String cameraId = null;
		try {
			cameraId = zCameraManager.getCameraIdList()[0];

			CameraCharacteristics zCameraCharacteristics = zCameraManager.getCameraCharacteristics(cameraId);

			StreamConfigurationMap zStreamConfigurationMap = zCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				Log.i("ZTest Permission", "NotAvailable");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
			}

			zCameraManager.openCamera(cameraId, stateCallBack, null);

		} catch (CameraAccessException e) {
			e.printStackTrace();
		}


	}

*/




















	/*
	CameraDevice mCamera;
	MediaRecorder mMediaRecorderLow = new MediaRecorder();
	MediaRecorder mMediaRecorderHigh = new MediaRecorder();
	CaptureRequest mCaptureRequest;
	CameraCaptureSession mSession;
	boolean recording = false;


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

			Log.i("ZTest Permission", "Granted");
		} else {
			Log.i("ZTest Permission", "Not Granted");

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_activity_testing);

		CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);


		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				, "Test");
		if (!dir.exists()) {
			boolean s = dir.mkdirs();
			if (!s) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setMessage("could not create dir");
				alert.show();
			}
		}
		final File outputFileLow = new File(dir.getAbsolutePath(), "testLow.mp4");
		final File outputFileHigh = new File(dir.getAbsolutePath(), "testHigh.mp4");


		try {
			outputFileLow.createNewFile();
			String[] cameras = manager.getCameraIdList();
			CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameras[0]);
			StreamConfigurationMap configs = characteristics.get(
					CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			Size[] sizes = configs.getOutputSizes(MediaCodec.class);
			final Size sizeLow = sizes[5];
			final Size sizeHigh = sizes[0];


			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				Log.i("ZTest Permission", "NotAvailable");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
			}


			manager.openCamera(cameras[0], new CameraDevice.StateCallback() {
				@Override
				public void onOpened(CameraDevice camera) {
					mCamera = camera;


					mMediaRecorderLow.setVideoSource(MediaRecorder.VideoSource.SURFACE);
					mMediaRecorderLow.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					mMediaRecorderLow.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
					mMediaRecorderLow.setVideoSize(sizeLow.getWidth(), sizeLow.getHeight());
					mMediaRecorderLow.setMaxFileSize(0);
					mMediaRecorderLow.setOrientationHint(0);

					mMediaRecorderHigh.setVideoSource(MediaRecorder.VideoSource.SURFACE);
					mMediaRecorderHigh.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					mMediaRecorderHigh.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
					mMediaRecorderHigh.setVideoSize(sizeHigh.getWidth(), sizeHigh.getHeight());
					mMediaRecorderHigh.setMaxFileSize(0);
					mMediaRecorderLow.setOrientationHint(0);


					try {
						mMediaRecorderLow.setOutputFile(outputFileLow.getAbsolutePath());
						mMediaRecorderHigh.setOutputFile(outputFileHigh.getAbsolutePath());
						mMediaRecorderLow.prepare();
						mMediaRecorderHigh.prepare();
						List<Surface> list = new ArrayList<>();
						list.add(mMediaRecorderLow.getSurface());
						list.add(mMediaRecorderHigh.getSurface());
						final CaptureRequest.Builder captureRequest = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
						captureRequest.addTarget(mMediaRecorderLow.getSurface());
						captureRequest.addTarget(mMediaRecorderHigh.getSurface());
						mCaptureRequest = captureRequest.build();
						mCamera.createCaptureSession(list, new CameraCaptureSession.StateCallback() {
							@Override
							public void onConfigured(CameraCaptureSession session) {
								mSession = session;
							}

							@Override
							public void onConfigureFailed(CameraCaptureSession session) {
								mSession = session;
							}
						}, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onDisconnected(CameraDevice camera) {

				}

				@Override
				public void onError(CameraDevice camera, int error) {

				}
			}, null);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	public void trigger(View v) {


		Log.i("Trigger Click", "Started");

		((Button) findViewById(R.id.buttonRecord)).setText("stop");
		try {
			if (!recording) {
				mMediaRecorderLow.start();
				mMediaRecorderHigh.start();
				mSession.setRepeatingRequest(mCaptureRequest,
						new CameraCaptureSession.CaptureCallback() {
							@Override
							public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
								super.onCaptureStarted(session, request, timestamp, frameNumber);
							}

							@Override
							public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
								super.onCaptureCompleted(session, request, result);
							}
						}, null);

				((Button) findViewById(R.id.buttonRecord)).setText("stop");
				recording = true;
			} else {
				((Button) findViewById(R.id.buttonRecord)).setText("start");
				recording = false;
				mSession.stopRepeating();
				mMediaRecorderHigh.stop();
				mMediaRecorderLow.stop();
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}
*/
