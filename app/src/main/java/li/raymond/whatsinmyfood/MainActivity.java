package li.raymond.whatsinmyfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	Button takePhotoButton;
	Button selectImageButton;
	Button analyzeImageButton;
	ImageView imagePreview;
	int SELECT_PICTURE = 200;
	Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		takePhotoButton = findViewById(R.id.takePhoto);
		selectImageButton = findViewById(R.id.selectImage);
		analyzeImageButton = findViewById(R.id.analyzeImage);
		imagePreview = findViewById(R.id.preview);

		takePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});

		selectImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseImage();
			}
		});

		analyzeImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				analyzeIngredients();
			}
		});
	}

	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);

		imageUri = Uri.fromFile(image);
		return image;
	}

	private void takePhoto() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				alertDialog(getString(R.string.createImageFileFailedTitle), getString(R.string.createImageFileFailedMsg) + ex.getMessage());
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"li.raymond.android.fileprovider",
						photoFile);
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	private void chooseImage() {
		Intent chooseImageIntent = new Intent();
		chooseImageIntent.setType("image/*");
		chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);

		// pass the constant to compare it with the returned requestCode
		startActivityForResult(Intent.createChooser(chooseImageIntent, "Select Picture"), SELECT_PICTURE);
	}

	// this function is triggered when user selects the image from the imageChooser
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && (requestCode == SELECT_PICTURE || requestCode == REQUEST_IMAGE_CAPTURE)) {
			// Get the url of the image from data
			imageUri = data.getData();
			if (null != imageUri) {
				// update the preview image in the layout
				imagePreview.setImageURI(imageUri);
			}
		}
	}

	private void analyzeIngredients() {
		Intent intent = new Intent(this, AnalyzeActivity.class);
		intent.putExtra("imageUri", imageUri.toString());
		startActivity(intent);
	}

	// Dialog used to send the user a message
	public void alertDialog(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).setMessage(message)
				.setTitle(title).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				}).create();
		alert.show();
		((TextView) alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}
}