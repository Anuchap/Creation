package com.izedtea.creation;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.samsung.spensdk.SCanvasConstants;

public class SPenSDKUtils {
	
	public static HashMap<String, Integer> getSettingLayoutLocaleResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting, boolean bUseFillingSetting){
		//----------------------------------------
        // Resource Map for Layout & Locale
		//----------------------------------------
		HashMap<String,Integer> settingResourceMapInt = new HashMap<String, Integer>();
		// Layout 
		if(bUsePenSetting){
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_PEN_SPINNER, R.layout.mspinner);
		}
//		if(bUseEraserSetting){
//			
//		}
		if(bUseTextSetting){
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER, R.layout.mspinnertext);
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER_TABLET, R.layout.mspinnertext_tablet);
		}
//		if(bUseFillingSetting){
//			
//		}
		
		//----------------------------------------
		// Locale(Multi-Language Support)	
		//----------------------------------------
		if(bUsePenSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_TITLE, R.string.pen_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EMPTY_MESSAGE, R.string.pen_settings_preset_empty);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_TITLE, R.string.pen_settings_preset_delete_title);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_MESSAGE, R.string.pen_settings_preset_delete_msg);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EXIST_MESSAGE, R.string.pen_settings_preset_exist);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_MAXIMUM_MESSAGE, R.string.pen_settings_preset_maximum_msg);
		}
		if(bUseEraserSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_TITLE, R.string.eraser_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_CLEARALL, R.string.clear_all);
		}
		if(bUseTextSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TITLE, R.string.text_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_FONT, R.string.text_settings_tab_font);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH, R.string.text_settings_tab_paragraph);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH_ALIGN, R.string.text_settings_tab_paragraph_align);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXTBOX_HINT, R.string.textbox_hint);
			
			settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME1, R.string.user_font_name1);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME2, R.string.user_font_name2);
		}
		if(bUseFillingSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_FILLING_SETTING_TITLE, R.string.filling_settings);
		}
		// common
		settingResourceMapInt.put(SCanvasConstants.LOCALE_SETTINGVIEW_CLOSE_DESCRIPTION, R.string.settingview_close_btn_desc);
	
		return settingResourceMapInt;
	}
	
	public static HashMap<String, String> getSettingLayoutStringResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting, boolean bUseFillingSetting){
		HashMap<String,String> settingResourceMapString = new HashMap<String, String>();
		if(bUseTextSetting){
			// Resource Map for Custom font path
			settingResourceMapString = new HashMap<String, String>();
			settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH1, "fonts/chococooky.ttf");
			settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH2, "fonts/rosemary.ttf");
		}
		
		// Set S Pen SDK Resource from Asset
		//settingResourceMapString.put(SCanvasConstants.CUSTOM_RESOURCE_ASSETS_PATH, "spen_sdk_resource");	// set folder of asstes/spen_sdk_resource
		
		return settingResourceMapString;
	}
	
	public static void alertActivityFinish(final Activity activity, String msg){
		AlertDialog.Builder ad = new AlertDialog.Builder(activity);		
		ad.setIcon(activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
		ad.setTitle(activity.getResources().getString(R.string.app_name))		
		.setMessage(msg)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				// finish dialog
				dialog.dismiss();
				activity.finish();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		})
		.show();
		ad = null;		
	}
}