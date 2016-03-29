package edu.uta.mavpark;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import edu.uta.mavpark.models.PermitModel;


public class PermitConfirmationFragment extends Fragment {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public PermitConfirmationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        PermitModel permit = (PermitModel) bundle.getSerializable("permitConfirmation");
        View rootView = inflater.inflate(R.layout.fragment_permit_confirmation, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.confirmation_qr);
        String qrContent = "UserId:" +permit.UserId+ "\n" + "LicensePlateId:" +permit.LicensePlateId+ "\n" + "ParkingLotId:"+ permit.ParkingLotId + "\n" + "ParkingSpaceId:"
                + permit.ParkingSpaceId + "\n" + "FromDateTime:" + permit.FromDateTime + "\n" + "ToDateTime:" + permit.ToDateTime;
        try {
            Bitmap bitmap = encodeAsBitmap(qrContent);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return rootView;
    }

        Bitmap encodeAsBitmap(String qrContent) throws WriterException {
            BitMatrix result;
            try {
                result = new MultiFormatWriter().encode(qrContent,
                        BarcodeFormat.QR_CODE, 850, 850, null);
            } catch (IllegalArgumentException iae) {
                // Unsupported format
                return null;
            }
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 850, 0, 0, w, h);
            return bitmap;
        }

}
