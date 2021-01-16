package li.raymond.whatsinmyfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;

public class AnalyzeActivity extends AppCompatActivity {
	Uri imageUri;
	InputImage image;
	Context context = getApplicationContext();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze);

		ImageView image = (ImageView) findViewById(R.id.DisplayImage);

		Intent intent = getIntent();
		String stringUri = intent.getExtras().getString("imageUri");
		imageUri = Uri.parse(stringUri);
		image.setImageURI(imageUri);
	}

	public void analyze() throws IOException {
		image = InputImage.fromFilePath(context, imageUri);
	}
}