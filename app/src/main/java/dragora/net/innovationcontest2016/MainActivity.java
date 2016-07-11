package dragora.net.innovationcontest2016;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.canelmas.let.AskPermission;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.Let;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thebluealliance.spectrum.SpectrumDialog;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MainActivity extends AppCompatActivity implements RuntimePermissionListener {

  private static final String TAG = MainActivity.class.getSimpleName();
  DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test");
  HashMap<String, String> obj = new HashMap<String, String>();
  FloatingActionButton fabJumpRight, fabJumpRightBoo;
  FloatingActionButton fabJumpLeft, fabJumpLeftBoo;
  FloatingActionButton fab;
  View colorLayout, photoLayout, shapeLayout, mainLayout;
  private String imgUrl =
      "https://scontent.xx.fbcdn.net/v/t1.0-9/10431668_10205233708436551_1811721222502137069_n.jpg?oh=c09b432dbf2bc99cf0019ea575539e42&oe=5835234F";
  private String circleImgUrl = imgUrl;
  ProgressBar progressSpinner;
  private int color;
  EditText nameInput;
  ImageView photoPreview, photoPreviewMain;
  Button joinGuestBook;

  private void uploadFile(File file) {
    Log.d(TAG, "uploadFile() called with: file = [" + file + "]");
    byte[] bytes = null;
    try {
      bytes = Glide.with(this)
          .load(file)

          .asBitmap()
          .toBytes(Bitmap.CompressFormat.PNG, 100)
          .transform(MainActivity.this.new MyCropCircleTransformation(MainActivity.this))
          .skipMemoryCache(true)
          .diskCacheStrategy(DiskCacheStrategy.NONE)
          .into(200, 200)
          .get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    // Points to the root reference
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    StorageReference riversRef = storageRef.child("images/luigi.png");
    String kid = nameInput.getText().toString();
    if (!TextUtils.isEmpty(kid)) {
      riversRef = storageRef.child("images/" + kid);
    }
    riversRef.putBytes(bytes)

        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // Get a URL to the uploaded content
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            Log.d(TAG, "onSuccess() called with: taskSnapshot = [" + downloadUrl + "]");
            circleImgUrl = downloadUrl.toString();
            obj.put("texture", circleImgUrl);
            setProgressStatus(false);
            runOnUiThread(new Runnable() {
              @Override public void run() {
                Glide.with(MainActivity.this).load(circleImgUrl).into(photoPreview);
                Glide.with(MainActivity.this).load(circleImgUrl).into(photoPreviewMain);
                joinGuestBook.setVisibility(View.VISIBLE);
              }
            });
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception exception) {

        Log.d(TAG, "onFailure() called with: exception = [" + exception + "]");
        setProgressStatus(false);
      }
    });
  }

  private void reveal(final View revealView, final View hideView) {
    runOnUiThread(new Runnable() {
      @Override public void run() {

        // previously invisible view
        View myView = revealView;
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animator) {

          }

          @Override public void onAnimationEnd(Animator animator) {
            hideView.setVisibility(View.GONE);
          }

          @Override public void onAnimationCancel(Animator animator) {

          }

          @Override public void onAnimationRepeat(Animator animator) {

          }
        });
        anim.setDuration(600);
        anim.start();
      }
    });
  }

  Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    obj.put("texture", circleImgUrl);
    obj.put("shape", "circle");

    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.remove("action");
        String kid = nameInput.getText().toString();
        if (!TextUtils.isEmpty(kid)) {
          obj.put("kid", kid);
        } else {
          obj.remove("kid");
        }
        obj.put("color", Integer.toHexString(color).toUpperCase().substring(2));
        ref.push().setValue(obj);
      }
    });

    fabJumpLeft = (FloatingActionButton) findViewById(R.id.fabJumpLeft);
    fabJumpLeft.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.put("action", "ljump");
        String kid = nameInput.getText().toString();
        if (!TextUtils.isEmpty(kid)) {
          obj.put("kid", kid);
        } else {
          obj.remove("kid");
        }
        obj.put("color", "#00AA00");
        ref.push().setValue(obj);
      }
    });
    fabJumpLeftBoo = (FloatingActionButton) findViewById(R.id.fabJumpLeftBoo);
    fabJumpLeftBoo.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.put("action", "ljump");
        String kid = nameInput.getText().toString();
        if (!TextUtils.isEmpty(kid)) {
          obj.put("kid", kid);
        } else {
          obj.remove("kid");
        }
        obj.put("color", "#AA0000");
        ref.push().setValue(obj);
      }
    });
    fabJumpRight = (FloatingActionButton) findViewById(R.id.fabJumpRight);
    fabJumpRight.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.put("action", "rjump");
        String kid = nameInput.getText().toString();
        if (!TextUtils.isEmpty(kid)) {
          obj.put("kid", kid);
        } else {
          obj.remove("kid");
        }
        obj.put("color", "#00AA00");

        ref.push().setValue(obj);
      }
    });
    fabJumpRightBoo = (FloatingActionButton) findViewById(R.id.fabJumpRightBoo);
    fabJumpRightBoo.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.put("action", "rjump");
        String kid = nameInput.getText().toString();
        if (!TextUtils.isEmpty(kid)) {
          obj.put("kid", kid);
        } else {
          obj.remove("kid");
        }
        obj.put("color", "#AA0000");
        ref.push().setValue(obj);
      }
    });

    nameInput = (EditText) findViewById(R.id.nameInput);
    colorLayout = findViewById(R.id.colorLayout);
    photoLayout = findViewById(R.id.photoLayout);
    shapeLayout = findViewById(R.id.shapeLayout);
    mainLayout = findViewById(R.id.mainLayout);
    progressSpinner = (ProgressBar) findViewById(R.id.progress_spinner);
    photoPreview = (ImageView) findViewById(R.id.photoPreview);
    photoPreviewMain = (ImageView) findViewById(R.id.photoPreviewMain);
    joinGuestBook = (Button) findViewById(R.id.joinGuestbook);
    int rand = (int) (Math.random() * 1000000d);
    nameInput.setText("Guest " + rand);
  }

  public void onJoinClick(View v) {
    fab.callOnClick();
    reveal(mainLayout, shapeLayout);
  }

  private void setProgressStatus(final boolean isVisible) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        progressSpinner.setVisibility(isVisible ? View.VISIBLE : View.GONE);
      }
    });
  }

  public void onShapeClick(View view) {
    switch (view.getId()) {
      case R.id.circle:
        obj.put("shape", "circle");
        break;
      case R.id.square:
        obj.put("shape", "square");
        break;
    }
  }

  public void onColorClick(final View v) {
    new SpectrumDialog.Builder(getBaseContext()).setColors(R.array.demo_colors)
        .setSelectedColorRes(R.color.md_blue_500)
        .setDismissOnColorSelected(true)
        .setFixedColumnCount(4)
        .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
          @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
            if (positiveResult) {
              obj.put("color", Integer.toHexString(color).toUpperCase().substring(2));
              MainActivity.this.color = color;
              Log.d(TAG, "pick() " + color);
              reveal(photoLayout, colorLayout);
            } else {

            }
          }
        })
        .build()
        .show(getSupportFragmentManager(), "dialog_demo_4");
  }

  public Context getContext() {
    return getBaseContext();
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    Let.handle(this, requestCode, permissions, grantResults);
  }

  @Override public void onShowPermissionRationale(List<String> permissions,
      final RuntimePermissionRequest request) {
    /**
     * you may show permission rationales in a dialog, wait for user confirmation and retry the permission
     * request by calling request.retry()
     */
  }

  @Override public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {
    /**
     * Do whatever you need to do about denied permissions:
     *   - update UI
     *   - if permission is denied with 'Never Ask Again', prompt a dialog to tell user
     *   to go to the app settings screen in order to grant again the permission denied
     */
  }

  public void onPhotoClick(View v) {
    startPickPhoto();
  }

  @AskPermission({ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA })
  public void startPickPhoto() {
    EasyImage.openChooserWithGallery(MainActivity.this, "pick selfie", 0);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
      @Override
      public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
        //Some error handling
      }

      @Override
      public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {
        //Handle the image
        new AsyncTask<Void, Void, Void>() {

          @Override protected void onPreExecute() {
            super.onPreExecute();
            setProgressStatus(true);
            reveal(shapeLayout, photoLayout);
          }

          @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
          }

          @Override protected Void doInBackground(Void... voids) {
            uploadFile(imageFile);
            return null;
          }
        }.execute();
      }

      @Override public void onCanceled(EasyImage.ImageSource source, int type) {
        //Cancel handling, you might wanna remove taken photo if it was canceled
        if (source == EasyImage.ImageSource.CAMERA) {
          File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
          if (photoFile != null) photoFile.delete();
        }
      }
    });
  }

  public class MyCropCircleTransformation implements Transformation<Bitmap> {

    private BitmapPool mBitmapPool;

    public MyCropCircleTransformation(Context context) {
      this(Glide.get(context).getBitmapPool());
    }

    public MyCropCircleTransformation(BitmapPool pool) {
      this.mBitmapPool = pool;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
      Log.d(TAG, "transform() called with: resource = ["
          + resource
          + "], outWidth = ["
          + outWidth
          + "], outHeight = ["
          + outHeight
          + "]");
      Bitmap source = resource.get();
      int size = Math.min(source.getWidth(), source.getHeight());

      int width = (source.getWidth() - size) / 2;
      int height = (source.getHeight() - size) / 2;

      final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f,
          MainActivity.this.getResources().getDisplayMetrics());

      Bitmap bitmap = mBitmapPool.get(size + borderSizePx * 2, size + borderSizePx * 2,
          Bitmap.Config.ARGB_8888);
      if (bitmap == null) {
        bitmap = Bitmap.createBitmap(size + borderSizePx * 2, size + borderSizePx * 2,
            Bitmap.Config.ARGB_8888);
      }

      Canvas canvas = new Canvas(bitmap);
      Paint paint = new Paint();
      BitmapShader shader =
          new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
      if (width != 0 || height != 0) {
        // source isn't square, move viewport to center
        Matrix matrix = new Matrix();
        matrix.setTranslate(-width, -height);
        shader.setLocalMatrix(matrix);
      }
      paint.setShader(shader);
      paint.setAntiAlias(true);

      float r = size / 2f;
      canvas.drawCircle(r + borderSizePx, r + borderSizePx, r, paint);

      Paint border = new Paint();
      border.setColor(color);
      border.setStyle(Paint.Style.STROKE);
      border.setStrokeWidth((float) borderSizePx);
      border.setAntiAlias(true);
      Log.d(TAG, "transform() " + color);
      canvas.drawCircle(r + borderSizePx, r + borderSizePx, r + (borderSizePx / 2), border);

      return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override public String getId() {
      return "MyCropCircleTransformation()";
    }
  }
}
