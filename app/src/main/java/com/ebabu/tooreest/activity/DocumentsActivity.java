package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.ImageUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DocumentsActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = DocumentsActivity.class.getSimpleName();
    private Context context;
    private CustomTextView tvIdProof, tvAddressProof, tvBankStatement, tvCertificate, btnUpdate;
    private CustomEditText etBankName, etAccountNum, etBicCode, etLicenseNum, etBranchCode, etPayPalId;
    private ImageView ivIdProof, ivAddressProof, ivBankStatement, ivCertificate;
    private RadioGroup rgLicensedGuide, rgPayPalId;
    private AppCompatRadioButton rbYes, rbNo, rbYesPaypal,rbNoPaypal;
    private LinearLayout layoutLicense,layoutBankDetails;
    private String idProofUrl, addressProofUrl, certificateUrl, bankStatementUrl, bankName, accountNum, bicCode, licenseNum, paypalId, branchCode;
    private final static int RQ_ID_PROOF = 1, RQ_ADDRESS_PROOF = 2, RQ_BANK_STATEMENT = 3, RQ_CERTIFICATE = 4, BRANCH_CODE_LENGTH = 3;
    private int selectedImageRequest, isLicensedGuide;
    private ImageUtils imageUtilsIdProof, imageUtilsAddressProof, imageUtilsBankStatement, imageUtilsCertificate;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        context = DocumentsActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.documents));
    }

    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        tvIdProof = (CustomTextView) findViewById(R.id.tv_id_prooft);
        tvAddressProof = (CustomTextView) findViewById(R.id.tv_address_proof);
        tvBankStatement = (CustomTextView) findViewById(R.id.tv_bank_passbook);
        tvCertificate = (CustomTextView) findViewById(R.id.tv_certificate);
        btnUpdate = (CustomTextView) findViewById(R.id.btn_update);
        etBankName = (CustomEditText) findViewById(R.id.et_bank_name);
        etAccountNum = (CustomEditText) findViewById(R.id.et_account_num);
        etPayPalId = (CustomEditText) findViewById(R.id.et_paypal_id);
        etBicCode = (CustomEditText) findViewById(R.id.et_ifsc_code);
        etLicenseNum = (CustomEditText) findViewById(R.id.et_license_number);
        etBranchCode = (CustomEditText) findViewById(R.id.et_bank_code);
        rgLicensedGuide = (RadioGroup) findViewById(R.id.rg_licensed_guide);
        rgLicensedGuide = (RadioGroup) findViewById(R.id.rg_paypal_account);

        rbNoPaypal= (AppCompatRadioButton)findViewById(R.id.rb_no_paypal);
        rbYesPaypal = (AppCompatRadioButton) findViewById(R.id.rb_yes_paypal);
        rbYes = (AppCompatRadioButton) findViewById(R.id.rb_yes);
        rbNo = (AppCompatRadioButton) findViewById(R.id.rb_no);

        layoutLicense = (LinearLayout) findViewById(R.id.layout_license);
        layoutBankDetails=(LinearLayout)findViewById(R.id.layout_bank_details);
        ivIdProof = (ImageView) findViewById(R.id.iv_id_proof);
        ivAddressProof = (ImageView) findViewById(R.id.iv_address_proof);
        ivBankStatement = (ImageView) findViewById(R.id.iv_bank_passbook);
        ivCertificate = (ImageView) findViewById(R.id.iv_certificate);


        ivIdProof.setOnClickListener(this);
        ivAddressProof.setOnClickListener(this);
        ivCertificate.setOnClickListener(this);
        ivBankStatement.setOnClickListener(this);
        tvIdProof.setOnClickListener(this);
        tvAddressProof.setOnClickListener(this);
        tvCertificate.setOnClickListener(this);
        tvBankStatement.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        imageUtilsIdProof = new ImageUtils(context, ivIdProof);
        imageUtilsAddressProof = new ImageUtils(context, ivAddressProof);
        imageUtilsCertificate = new ImageUtils(context, ivCertificate);
        imageUtilsBankStatement = new ImageUtils(context, ivBankStatement);

        rbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    layoutLicense.setVisibility(View.VISIBLE);
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(scrollView.FOCUS_DOWN);
                        }
                    });
                } else {
                    layoutLicense.setVisibility(View.GONE);
                }
            }
        });

        rbYesPaypal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    etPayPalId.setVisibility(View.VISIBLE);
                    layoutBankDetails.setVisibility(View.GONE);
                } else {
                    etPayPalId.setVisibility(View.GONE);
                    layoutBankDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        setDataInView();
    }

    private void setDataInView() {
        AppPreference appPreference = AppPreference.getInstance(context);
        idProofUrl = appPreference.getIdProofUrl();
        addressProofUrl = appPreference.getAddressProofUrl();
        bankName = appPreference.getBankName();
        accountNum = appPreference.getBankAccountNum();
        bicCode = appPreference.getBicCode();
        branchCode = appPreference.getBranchCode();
        paypalId = appPreference.getPaypalId();
        int hasPayPalId = appPreference.hasPayPalId();
        isLicensedGuide = appPreference.isLicensedGuide();
        certificateUrl = appPreference.getCertificateUrl();
        bankStatementUrl = appPreference.getBankIdUrl();
        licenseNum = appPreference.getLicenseNumber();

        AQuery aQuery = new AQuery(context);
        if (idProofUrl.startsWith(IKeyConstants.HTTP)) {
            aQuery.id(ivIdProof).image(idProofUrl, true, true, 80, AQuery.FADE_IN);
        }
        if (addressProofUrl.startsWith(IKeyConstants.HTTP)) {
            aQuery.id(ivAddressProof).image(addressProofUrl, true, true, 80, AQuery.FADE_IN);
        }
        if (bankStatementUrl.startsWith(IKeyConstants.HTTP)) {
            aQuery.id(ivBankStatement).image(bankStatementUrl, true, true, 80, AQuery.FADE_IN);
        }
        if (certificateUrl.startsWith(IKeyConstants.HTTP)) {
            aQuery.id(ivCertificate).image(certificateUrl, true, true, 80, AQuery.FADE_IN);
        }
        etBankName.setText(bankName);
        etAccountNum.setText(accountNum);
        etBicCode.setText(bicCode);
        etLicenseNum.setText(licenseNum);
        etPayPalId.setText(paypalId);
        etBranchCode.setText(branchCode);

        if (isLicensedGuide == 1) {
            rbYes.setChecked(true);
            layoutLicense.setVisibility(View.VISIBLE);
        } else {
            rbNo.setChecked(true);
            layoutLicense.setVisibility(View.GONE);
        }

        if (hasPayPalId == 1) {
            rbYesPaypal.setChecked(true);
            etPayPalId.setVisibility(View.VISIBLE);
        } else {
            rbYesPaypal.setChecked(false);
            etPayPalId.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (selectedImageRequest == RQ_ID_PROOF) {
                imageUtilsIdProof.onActivityResult(requestCode, resultCode, data);
            } else if (selectedImageRequest == RQ_ADDRESS_PROOF) {
                imageUtilsAddressProof.onActivityResult(requestCode, resultCode, data);
            } else if (selectedImageRequest == RQ_BANK_STATEMENT) {
                imageUtilsBankStatement.onActivityResult(requestCode, resultCode, data);
            } else if (selectedImageRequest == RQ_CERTIFICATE) {
                imageUtilsCertificate.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private boolean areFieldsValid() {

        bankName = etBankName.getText().toString();
        accountNum = etAccountNum.getText().toString();
        bicCode = etBicCode.getText().toString();
        licenseNum = etLicenseNum.getText().toString();
        branchCode = etBranchCode.getText().toString();
        paypalId = etPayPalId.getText().toString();

        if (idProofUrl.isEmpty()) {
            if (imageUtilsIdProof.getByteArray() == null) {
                Toast.makeText(context, getString(R.string.upload_id_proof), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (addressProofUrl.isEmpty()) {
            if (imageUtilsAddressProof.getByteArray() == null) {
                Toast.makeText(context, getString(R.string.upload_address_proof), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (rbYesPaypal.isChecked()) {
            if (paypalId.isEmpty()) {
                etPayPalId.setError(getString(R.string.enter_paypal_id));
                return false;
            }
            if (!Utils.isValidEmail(paypalId)) {
                etPayPalId.setError(getString(R.string.enter_a_valid_email));
                return false;
            }
            bankName = IKeyConstants.EMPTY;
            etBankName.setText(bankName);
            accountNum = IKeyConstants.EMPTY;
            etAccountNum.setText(accountNum);
            bicCode = IKeyConstants.EMPTY;
            etBicCode.setText(bicCode);
            branchCode = IKeyConstants.EMPTY;
            etBranchCode.setText(branchCode);
        }
        else {
            paypalId = IKeyConstants.EMPTY;
            etPayPalId.setText(paypalId);

            if (bankName.isEmpty()) {
                etBankName.setError(getString(R.string.enter_bank_name));
                return false;
            }

            if (!Utils.isNameValid(bankName)) {
                etBankName.setError(getString(R.string.enter_a_valid_bank_name));
                return false;
            }

            if (accountNum.isEmpty()) {
                etAccountNum.setError(getString(R.string.enter_account_number));
                return false;
            }

            if (bicCode.isEmpty()) {
                etBicCode.setError(getString(R.string.enter_ifsc_code));
                return false;
            }

            if (!Utils.isBicCodeValid(bicCode)) {
                etBicCode.setError("Invalid SWIFT/BIC Code");
                DialogUtils.openAlertToShowMessage(getString(R.string.invalid_bic_code), context);
                return false;
            }

            if (branchCode.isEmpty()) {
                etBranchCode.setError(getString(R.string.enter_branch_code));
                return false;
            }

          /* if (branchCode.length() < BRANCH_CODE_LENGTH) {
             etBranchCode.setError(getString(R.string.invalid_branch_code, BRANCH_CODE_LENGTH));
             return false;
          }*/

            if (bankStatementUrl.isEmpty()) {
                if (imageUtilsBankStatement.getByteArray() == null) {
                    Toast.makeText(context, getString(R.string.upload_bank_statement_or_passbook), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        }

        if (rbYes.isChecked()) {
            if (certificateUrl.isEmpty()) {
                if (imageUtilsCertificate.getByteArray() == null) {
                    Toast.makeText(context, getString(R.string.upload_certificate), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (licenseNum.isEmpty()) {
                etLicenseNum.setError(getString(R.string.enter_license_number));
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_id_prooft:
                selectedImageRequest = RQ_ID_PROOF;
                imageUtilsIdProof.openDialogToChosePic();
                break;

            case R.id.tv_address_proof:
                selectedImageRequest = RQ_ADDRESS_PROOF;
                imageUtilsAddressProof.openDialogToChosePic();
                break;

            case R.id.tv_bank_passbook:
                selectedImageRequest = RQ_BANK_STATEMENT;
                imageUtilsBankStatement.openDialogToChosePic();
                break;

            case R.id.tv_certificate:
                selectedImageRequest = RQ_CERTIFICATE;
                imageUtilsCertificate.openDialogToChosePic();
                break;

            case R.id.btn_update:
                updateDocuments();
                break;

            case R.id.iv_id_proof:
                openZoomActivity(idProofUrl);
                break;

            case R.id.iv_address_proof:
                openZoomActivity(addressProofUrl);
                break;

            case R.id.iv_certificate:
                openZoomActivity(certificateUrl);
                break;

            case R.id.iv_bank_passbook:
                openZoomActivity(bankStatementUrl);
                break;
        }
    }

    private void openZoomActivity(String imgUrl) {
        if (imgUrl != null && !imgUrl.isEmpty() && imgUrl.startsWith(IKeyConstants.HTTP)) {
            Intent intent = new Intent(context, ZoomActivity.class);
            intent.putExtra(IKeyConstants.IMAGE, imgUrl);
            startActivity(intent);
        }
    }

    private void updateDocuments() {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!areFieldsValid()) {
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        Map<String, Object> params = new HashMap<>();
        if (imageUtilsIdProof.getByteArray() != null) {
            params.put(IKeyConstants.KEY_ID_IMG, imageUtilsIdProof.getByteArray());
        } else {
            params.put(IKeyConstants.KEY_ID_IMG, IKeyConstants.EMPTY);
        }

        if (imageUtilsAddressProof.getByteArray() != null) {
            params.put(IKeyConstants.KEY_ADDRESS_IMG, imageUtilsAddressProof.getByteArray());
        } else {
            params.put(IKeyConstants.KEY_ADDRESS_IMG, IKeyConstants.EMPTY);
        }



        if (rbYes.isChecked()) {
            params.put(IKeyConstants.KEY_LICENSE_NUM, licenseNum);
            if (imageUtilsCertificate.getByteArray() != null) {
                params.put(IKeyConstants.KEY_LICENSE_IMG, imageUtilsCertificate.getByteArray());
            } else {
                params.put(IKeyConstants.KEY_LICENSE_IMG, IKeyConstants.EMPTY);
            }
        } else {
            licenseNum = IKeyConstants.EMPTY;
            params.put(IKeyConstants.KEY_LICENSE_NUM, IKeyConstants.EMPTY);
            params.put(IKeyConstants.KEY_LICENSE_IMG, IKeyConstants.EMPTY);
        }
        params.put(IKeyConstants.KEY_USER_NAME, AppPreference.getInstance(context).getFullName());


        params.put(IKeyConstants.KEY_BANK_NAME, bankName);
        params.put(IKeyConstants.KEY_ACCOUNT_NUM, accountNum);
        params.put(IKeyConstants.KEY_BIC_CODE, bicCode);
        params.put(IKeyConstants.KEY_BANK_CODE, branchCode);

        if (imageUtilsBankStatement.getByteArray() != null) {
            params.put(IKeyConstants.KEY_BANK_IMG, imageUtilsBankStatement.getByteArray());
        } else {
            params.put(IKeyConstants.KEY_BANK_IMG, IKeyConstants.EMPTY);
        }

        params.put(IKeyConstants.KEY_HAS_PAYPAL_ACCOUNT, (rbYesPaypal.isChecked() ? 1 : 0) + IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_PAYPAL_EMAIL_ID, paypalId);

        Log.d(TAG, "updateDocuments(): params=" + params);

        new AQuery(context).ajax(IUrlConstants.UPLOAD_LICENSE_DOC, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "updateDocuments(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONObject jsonObject = json.getJSONObject("data");
                            AppPreference.getInstance(context).setBankName(bankName);
                            AppPreference.getInstance(context).setBankAccountNum(accountNum);
                            AppPreference.getInstance(context).setBicCode(bicCode);

                            AppPreference.getInstance(context).setLicenseNumber(licenseNum);

                            AppPreference.getInstance(context).setIdProofUrl(jsonObject.getString(IKeyConstants.KEY_ID_IMG));
                            AppPreference.getInstance(context).setBankIdUrl(jsonObject.getString(IKeyConstants.KEY_BANK_IMG));
                            AppPreference.getInstance(context).setAddressProofUrl(jsonObject.getString(IKeyConstants.KEY_ADDRESS_IMG));
                            AppPreference.getInstance(context).setCertificateUrl(jsonObject.getString(IKeyConstants.KEY_LICENSE_IMG));

                            AppPreference.getInstance(context).setHasPaypalId(rbYesPaypal.isChecked() ? 1 : 0);
                            AppPreference.getInstance(context).setBranchCode(branchCode);
                            AppPreference.getInstance(context).setPaypalId(paypalId);

                            Toast.makeText(context, getString(R.string.documents_updated_successfully), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
