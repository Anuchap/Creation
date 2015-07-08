package com.izedtea.creation;

import java.util.HashMap;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ViewPhotoActivity extends Activity {

	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 2;
	private final int APPLICATION_ID_VERSION_MINOR = 2;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";
	
	Context mContext = null;
	
	private FrameLayout	mLayoutContainer;
	private RelativeLayout mCanvasContainer;
	private SCanvasView	mSCanvas;
	private ImageView mPenBtn;
	private ImageView mEraserBtn;
	private ImageView mTextBtn;
	private ImageView mUndoBtn;
	private ImageView mRedoBtn;
	
	private Bitmap 	mBGBitmap;
	private Rect mSrcImageRect = null;
	
	private final int CANVAS_HEIGHT_MARGIN = 160; // Top,Bottom margin  
	private final int CANVAS_WIDTH_MARGIN = 50; // Left,Right margin
	
	private EditText noteEditText;
	
	private JobEntity job;
	private PhotoEntity photo;
	
	@Override
	public void onBackPressed() {
		PhotoModel photoModel = new PhotoModel(ViewPhotoActivity.this);
		
		ContentValues note = new ContentValues();
		note.put("note", noteEditText.getText().toString());
		photoModel.update(photo.getId(), note);
		
		Bitmap b = mSCanvas.getBitmap(false);
		
		BitmapHelper.bitmapToFile(b, MainActivity.BESEPATH + photo.getJobId() + "/" + photo.getPhoto(), 100);
		BitmapHelper.bitmapToFile(BitmapHelper.toThumbnail(b), MainActivity.BESEPATH + photo.getJobId() + "/thumbs/" + photo.getPhoto(), 100);
		
		Toast.makeText(ViewPhotoActivity.this, "saved.", Toast.LENGTH_SHORT).show();
				
		Intent intent = new Intent(ViewPhotoActivity.this, NewJobActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("job", job);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {	
		super.onDestroy();
		// Release SCanvasView resources
		mSCanvas.closeSCanvasView();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewphoto);
		
        mContext = this;
        
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		
		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
		
		noteEditText = (EditText)findViewById(R.id.noteEditText);
		
		job = (JobEntity)getIntent().getSerializableExtra("job");
		photo = (PhotoEntity)getIntent().getSerializableExtra("photo");
		
		noteEditText.setText(photo.getNote());
        
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
        mCanvasContainer = (RelativeLayout)findViewById(R.id.canvas_container);
        
        mSCanvas = new SCanvasView(mContext);
        mCanvasContainer.addView(mSCanvas);
        
		mBGBitmap = BitmapFactory.decodeFile(MainActivity.BESEPATH + photo.getJobId() + "/" + photo.getPhoto());
		if(mBGBitmap != null){
			mSrcImageRect = new Rect(0,0,mBGBitmap.getWidth(), mBGBitmap.getHeight());
		}
		
        setSCanvasViewLayout();
		// Set Background of layout container
		//mLayoutContainer.setBackgroundResource(R.drawable.bg_edit);
        
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);
		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);
        
        mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			public void onInitialized() {
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				if(!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();
				
				mSCanvas.setBGImage(mBGBitmap);
				
				updateModeState();
			}
		});
        
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		});
		
		mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {
			public void onModeChanged(int mode) {
				updateModeState();
			}
		});
        
		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);
	}
	
	private void setSCanvasViewLayout(){
		Rect rectCanvas = getMaximumCanvasRect(mSrcImageRect, CANVAS_WIDTH_MARGIN, CANVAS_HEIGHT_MARGIN);
		int nCurWidth = rectCanvas.right-rectCanvas.left;
		int nCurHeight = rectCanvas.bottom-rectCanvas.top;
		// Place SCanvasView In the Center
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mCanvasContainer.getLayoutParams();		
		layoutParams.width = nCurWidth;
		layoutParams.height= nCurHeight;
		layoutParams.gravity = Gravity.CENTER; 
		mCanvasContainer.setLayoutParams(layoutParams);
	}
	
	OnClickListener mBtnClickListener = new OnClickListener() {
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);					
					updateModeState();
				}
			}
			else if(nBtnID == mEraserBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			}
			else if(nBtnID == mTextBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
			}		
		}
	};
	
	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				 mSCanvas.undo();
			}
			else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};
    
	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
		mTextBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
	}
	
	private Rect getMaximumCanvasRect(Rect rectImage, int nMarginWidth, int nMarginHeight){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int nScreenWidth =  displayMetrics.widthPixels - nMarginWidth;
		int nScreenHeight = displayMetrics.heightPixels - nMarginHeight;

		int nImageWidth = rectImage.right - rectImage.left;
		int nImageHeight = rectImage.bottom - rectImage.top;			
		
		float fResizeWidth = (float) nScreenWidth / nImageWidth;
		float fResizeHeight = (float) nScreenHeight / nImageHeight;
		float fResizeRatio;
		
		// Fit to Height
		if(fResizeWidth>fResizeHeight){
			fResizeRatio = fResizeHeight;
		}
		// Fit to Width
		else {	
			fResizeRatio = fResizeWidth;
		}
		//return new Rect(0,0, (int)(nImageWidth*fResizeRatio), (int)(nImageHeight*fResizeRatio));
		// Adjust more detail
		int nResizeWidth = (int)(nImageWidth*fResizeRatio);
		int nResizeHeight = (int)(0.5 + (nResizeWidth * nImageHeight)/(float)nImageWidth);		
		return new Rect(0,0, nResizeWidth, nResizeHeight);
	}
}
