/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techease.posapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


public class InternetUtils {
    public static final String _2G = "2G";
    public static final String _3G = "3G";
    public static final String _4G = "4G";
    public static final String WIFI = "Wifi";
    public static final String BLUETOOTH = "Bluetooth";
    public static final String WIMAX = "Wimax";
    public static final String[] MOBILE_NETWORK_TYPES = {_2G, _3G, _4G};
    public static String UNKNOWN = "Unknown";

    public static boolean isNetworkConnected(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkConnectingOrConnected(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isWiFiEnabled(final Context context) {
        final WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return false;
        }
        return wifiManager.isWifiEnabled();
    }

    public static void showWiFiDialog(final FragmentActivity activity) {
//        DialogUtils.showErrorDialogWithTitle(activity,
//                string(R.string.no_network_connection),
//                string(R.string.error_no_network), string(R.string.setting),
//                string(R.string.cancel), new DialogUtils.DialogCallBackAdapter() {
//                    @Override
//                    public void onPositiveClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        super.onPositiveClick(dialog, which);
//                        final Intent wifiIntent = new Intent(
//                                Settings.ACTION_SETTINGS);
//                        wifiIntent
//                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                        activity.startActivity(wifiIntent);
//                    }
//                });
    }

    public static boolean isConnectedToInternet(FragmentActivity activity) {
        if (!InternetUtils.isConnected(activity)) {
            InternetUtils.showWiFiDialog(activity);
            return false;
        }
        return true;
    }

//	public static class WiFiDialog extends DialogFragment {
//
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			final int padding = getResources().getDimensionPixelSize(
//					R.dimen.content_padding_normal);
//			final TextView wifiTextView = new TextView(getActivity());
//			int dialogCallToActionText;
//			int dialogPositiveButtonText;
//			dialogCallToActionText = R.string.calltoaction_settings;
//			dialogPositiveButtonText = R.string.Internet_dialog_button_settings;
//
//			wifiTextView.setText(Html
//					.fromHtml(getString(R.string.description_setup_internet_body)
//							+ getString(dialogCallToActionText)));
//			wifiTextView.setMovementMethod(LinkMovementMethod.getInstance());
//			wifiTextView.setPadding(padding, padding, padding, padding);
//			wifiTextView.setBackgroundColor(Color.WHITE);
//			final Context context = getActivity();
//
//			return new AlertDialog.Builder(context)
//					.setTitle(R.string.description_configure_internet)
//					.setView(wifiTextView)
//					.setPositiveButton(dialogPositiveButtonText,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//													int whichButton) {
//									final Intent wifiIntent = new Intent(
//											Settings.ACTION_SETTINGS);
//									wifiIntent
//											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//									startActivity(wifiIntent);
//									dialog.dismiss();
//								}
//							}).create();
//		}
//	}


    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static void checkInternetConnection(FragmentActivity activity) {
        if (!isNetworkConnected(activity)) {

        }
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public static String getNetworkType(Context context) {
        try {
            NetworkInfo info = getNetworkInfo(context);
            int type = info.getType();
            int subType = info.getSubtype();
            if (type == ConnectivityManager.TYPE_WIMAX) {
                return WIMAX;
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                return WIFI;
            } else if (type == ConnectivityManager.TYPE_BLUETOOTH) {
                return BLUETOOTH;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return _2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return _3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return _4G;
                    default:
                        return UNKNOWN;
                }
            } else {
                return UNKNOWN;
            }
        } catch (NullPointerException exception) {
            return UNKNOWN;
        }
    }

    public static class ConnectivityChangeReceiver extends BroadcastReceiver {
        final Context context;
        final IntentFilter intentFilter;

        public ConnectivityChangeReceiver(Context context) {
            this.context = context;
            intentFilter = new IntentFilter(CONNECTIVITY_ACTION);
        }

        public void unRegister() {
            context.unregisterReceiver(this);
        }

        public void register() {
            context.registerReceiver(this, intentFilter);
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnected(context)) {
                context.unregisterReceiver(this);
            }
        }
    }
}
