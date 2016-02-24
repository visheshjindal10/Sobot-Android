package exp.com.sobot;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriterFragment extends Fragment implements TextToSpeech.OnInitListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Writer Fragment";
    private static final String TTS_UNIQUE_ID = "UniqueID" ;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextToSpeech tts;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIsListening = false;
    private RecognitionListener mSpeechRecognizerListner;
    private EditText tvTextEditor;
    private FloatingActionButton fabWriter,fabReader;
    private ProgressDialog progressDialog;
    private StringBuffer dication = new StringBuffer();
    private boolean isSpeaking;


    public WriterFragment() {
        // Required empty public constructor
    }


    /**
     * Method to create new Instance of Fragment
     *
     * @return Fragment
     */
    public static WriterFragment newInstance() {
        WriterFragment fragment = new WriterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        tts = new TextToSpeech(getActivity(),this);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                isSpeaking = false;
                fabReader.post(new Runnable() {
                    @Override
                    public void run() {
                        fabReader.setImageResource(R.drawable.ic_fab_reader);
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
        initalizeSpeechRecongizer();
    }


    /**
     * Method to initialize speech Intent
     */
    private void initalizeSpeechRecongizer() {
        mSpeechRecognizer = getSpeechRecognizer();
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity()
                .getPackageName());
        mSpeechRecognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra("android.speech.extras" +
                ".SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 2000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_writer, container, false);
        viewInitialize(view);
        intializeProgressDialog();
        return view;
    }

    /**
     * Function to initialize Views
     * @param view View
     */
    private void viewInitialize(View view) {
        tvTextEditor = (EditText) view.findViewById(R.id.tvTextEditor);
        fabWriter = (FloatingActionButton) view.findViewById(R.id.fabWriter);
        fabWriter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsListening) {
                    fabReader.setEnabled(false);
                    startListening();
                } else {
                    fabReader.setEnabled(true);
                    stopListening();
                }
            }
        });

        fabReader = (FloatingActionButton) view.findViewById(R.id.fabReader);
        tvTextEditor = (EditText) view.findViewById(R.id.tvTextEditor);
        fabReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvTextEditor.getText().toString())){
                    Snackbar.make(fabReader, R.string.tts_error_no_text,Snackbar.LENGTH_LONG)
                            .show();
                }else {
                    if (!isSpeaking) {
                        startSpeaking();
                    } else {
                        stopSpeaking();
                    }

                }
            }
        });

    }

    /**
     * Function to start speaking
     */
    private void stopSpeaking() {
        fabWriter.setEnabled(true);
        tts.stop();
        isSpeaking = false;
        fabReader.setImageResource(R.drawable.ic_fab_reader);
        Snackbar.make(fabReader, R.string.tv_stop_Reading,Snackbar.LENGTH_LONG).show();
    }

    /**
     * Function to stop speaking
     */
    private void startSpeaking() {
        fabWriter.setEnabled(false);
        isSpeaking =true;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,TTS_UNIQUE_ID );
        tts.speak(tvTextEditor.getText().toString(), TextToSpeech.QUEUE_FLUSH,map);
        fabReader.setImageResource(R.drawable.ic_fab_stop_tts);
        Snackbar.make(fabReader, R.string.tv_reading_for_you,Snackbar.LENGTH_LONG).show();
    }

    /**
     * Function to initialize progress dialog
     */
    private void intializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(getActivity().getString(R.string.tv_Listening));
    }

    /**
     * Function Call to start Listening
     */
    private void startListening() {
        mSpeechRecognizerListner = new SpeechRecognitionListener();
        getSpeechRecognizer().setRecognitionListener(mSpeechRecognizerListner);
        getSpeechRecognizer().startListening(mSpeechRecognizerIntent);
        fabWriter.setImageResource(R.drawable.ic_fab_stop_write);
        Snackbar.make(fabWriter, R.string.speech_prompt, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Function Call to start Listening
     */
    private void stopListening() {
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.destroy();
        fabWriter.setImageResource(R.drawable.ic_fab_writer);
        mIsListening = false;
    }


    protected class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            //Log.d(TAG, "onBeginingOfSpeech");
            progressDialog.show();
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            startListening();
        }

        @Override
        public void onError(int error) {
            startListening();
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            setPartialResultToEditTextView(partialResults);
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
            mIsListening = true;
        }

        @Override
        public void onResults(Bundle results) {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            final ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            assert matches != null;
            dication.append(matches.get(0));
            tvTextEditor.setText(dication);
            Log.d("onResults:", dication.toString());
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do\
            stopListening();
            startListening();
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }
    }

    /**
     * Function to set Partial Results to EditText
     * @param partialResults Results
     */
    private void setPartialResultToEditTextView(Bundle partialResults) {
        final ArrayList<String> matches = partialResults.getStringArrayList
                (SpeechRecognizer.RESULTS_RECOGNITION);
        tvTextEditor.post(new Runnable() {
            @Override
            public void run() {
                assert matches != null;
                if (TextUtils.isEmpty(dication.toString())) {
                    tvTextEditor.setText(String.format(" %s", matches.get(0)));
                    Log.d("onPartialEmpty:", tvTextEditor.getText().toString());
                } else {
                    tvTextEditor.setText(String.format("%s %s", dication.toString(), matches.get(0)));
                    Log.d("inEditor:", tvTextEditor.getText().toString());
                }
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onStop() {
        if (mSpeechRecognizer != null) {
            stopListening();
        }
        super.onStop();
    }

    /**
     * lazy initialize the speech recognizer
     */
    private SpeechRecognizer getSpeechRecognizer()
    {
        if (mSpeechRecognizer == null)
        {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        }
        return mSpeechRecognizer;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar.make(fabReader, R.string.error_tts, Snackbar.LENGTH_LONG).show();
            } else {
                fabReader.setEnabled(true);
            }

        } else {
            fabReader.setEnabled(false);
            Snackbar.make(fabReader, R.string.error_tts_init_error, Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


}
