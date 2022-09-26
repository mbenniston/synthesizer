package synthesizer.Config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import synthesizer.Util.SampleLoader;
import synthesizer.Util.Tone;
import synthesizer.WaveForms.AdditiveWave;
import synthesizer.WaveForms.Envelope;
import synthesizer.WaveForms.Instrument;
import synthesizer.WaveForms.ModulatedWave;
import synthesizer.WaveForms.SampleWave;
import synthesizer.WaveForms.SawWave;
import synthesizer.WaveForms.SineWave;
import synthesizer.WaveForms.SquareWave;
import synthesizer.WaveForms.TriangleWave;
import synthesizer.WaveForms.Voice;
import synthesizer.WaveForms.WaveForm;

public class InstrumentReader {

    public static class LoadedInstrument {
        public final Instrument instrument;
        public final int programStartID, programEndID;

        public LoadedInstrument(Instrument instrument, int programStartID, int programEndID) {
            this.instrument = instrument;
            this.programStartID = programStartID;
            this.programEndID = programEndID;
        }
    }

    HashMap<String, WaveForm> namedWaves = new HashMap<>();
    HashMap<String, Envelope> namedEnvelopes = new HashMap<>();
    HashMap<String, Voice> namedVoices = new HashMap<>();

    public static Map<String, LoadedInstrument> load(InputStream stream)
            throws IOException, JSONException, UnsupportedAudioFileException, CloneNotSupportedException {
        return new InstrumentReader().read(stream);
    }

    public Map<String, LoadedInstrument> read(InputStream stream)
            throws IOException, JSONException, UnsupportedAudioFileException, CloneNotSupportedException {
        JSONObject rootObject = readJsonObjectFromStream(stream);

        if (rootObject.has("namedWaves")) {
            JSONObject namedVoicesObjects = rootObject.getJSONObject("namedWaves");

            for (String key : namedVoicesObjects.keySet()) {
                namedWaves.put(key, loadWaveFromJson(namedVoicesObjects.getJSONObject(key)));
            }
        }

        if (rootObject.has("namedEnvelopes")) {
            JSONObject namedEnvelopeObjects = rootObject.getJSONObject("namedEnvelopes");

            for (String key : namedEnvelopeObjects.keySet()) {
                namedEnvelopes.put(key, loadEnvelopeFromJson(namedEnvelopeObjects.getJSONObject(key)));
            }
        }

        if (rootObject.has("namedVoices")) {
            JSONObject namedVoiceObjects = rootObject.getJSONObject("namedVoices");

            for (String key : namedVoiceObjects.keySet()) {
                namedVoices.put(key,
                        loadVoiceFromJson(namedVoiceObjects.getJSONObject(key), namedWaves, namedEnvelopes));
            }
        }

        HashMap<String, LoadedInstrument> instruments = new HashMap<>();

        if (rootObject.has("namedInstruments")) {
            JSONObject namedInstrumentObjects = rootObject.getJSONObject("namedInstruments");

            for (String key : namedInstrumentObjects.keySet()) {
                instruments.put(key,
                        loadInstrument(namedInstrumentObjects.getJSONObject(key), namedWaves, namedEnvelopes,
                                namedVoices));
            }
        }

        return instruments;
    }

    private static class Note {
        public final Voice voice;
        public final int noteID;

        public Note(Voice voice, int noteID) {
            this.voice = voice;
            this.noteID = noteID;
        }
    }

    private LoadedInstrument loadInstrument(JSONObject instrumentObject, HashMap<String, WaveForm> waveForms,
            HashMap<String, Envelope> envelopes, HashMap<String, Voice> namedVoices)
            throws FileNotFoundException, JSONException, IOException, UnsupportedAudioFileException,
            CloneNotSupportedException {

        JSONObject programRange = instrumentObject.getJSONObject("programRange");
        int programStartID = programRange.getInt("start");
        int programEndID = programRange.getInt("end");

        JSONArray noteObjects = instrumentObject.getJSONArray("notes");

        ArrayList<Note> notes = new ArrayList<>();

        for (Object object : noteObjects) {
            JSONObject noteJson = (JSONObject) object;
            Voice voice = loadVoiceFromJson(noteJson.get("voice"), waveForms, envelopes);

            notes.add(new Note(voice, noteJson.getInt("noteId")));
        }

        Voice[] voices = new Voice[128];
        for (int i = 0; i < 128; i++) {
            Note closestNote = null;

            for (Note note : notes) {
                if (note.noteID == i) {
                    closestNote = note;
                    break;
                }

                if (closestNote == null || Math.abs(i - note.noteID) < Math.abs(i - closestNote.noteID)) {
                    closestNote = note;
                }
            }

            double frequencyScale = Tone.getFrequencyFromTone(i) / Tone.getFrequencyFromTone(closestNote.noteID);

            Voice newVoice = closestNote.voice.clone();
            newVoice.waveForm.setFrequency(newVoice.waveForm.getFrequency() * frequencyScale);

            voices[i] = newVoice;
        }

        Instrument instrument = new Instrument(voices);

        return new LoadedInstrument(instrument, programStartID, programEndID);
    }

    private Voice loadVoiceFromJson(Object voiceObject, Map<String, WaveForm> waveForms,
            Map<String, Envelope> envelopes)
            throws FileNotFoundException, JSONException, IOException, UnsupportedAudioFileException {

        if (voiceObject instanceof String) {
            return namedVoices.get((String) voiceObject);
        } else if (voiceObject instanceof JSONObject) {
            return new Voice(
                    loadWaveFromJson(((JSONObject) voiceObject).get("waveForm")),
                    loadEnvelopeFromJson(((JSONObject) voiceObject).get("envelope")));
        }

        return null;
    }

    private WaveForm loadWaveFromJson(Object waveValue)
            throws FileNotFoundException, IOException, UnsupportedAudioFileException {
        if (waveValue instanceof String) {
            return namedWaves.get((String) waveValue);

        } else if (waveValue instanceof JSONObject) {
            JSONObject waveObject = (JSONObject) waveValue;

            WaveType waveType = WaveType.GetType(waveObject.getString("waveType"));

            switch (waveType) {
                case TypeModulated:
                    return loadModulatedWave(waveObject);
                case TypeAdditive:
                    return loadAdditiveWave(waveObject);
                case TypeSine:
                    return loadSimpleWave(waveObject, new SineWave());
                case TypeSaw:
                    return loadSimpleWave(waveObject, new SawWave());
                case TypeSquare:
                    return loadSimpleWave(waveObject, new SquareWave());
                case TypeTriangle:
                    return loadSimpleWave(waveObject, new TriangleWave());
                case TypeSample:
                    return loadSampleWave(waveObject);
                default:
                    break;
            }
        }
        return null;
    }

    private Envelope loadEnvelopeFromJson(Object envelopeValue) {
        if (envelopeValue instanceof String) {
            return namedEnvelopes.get((String) envelopeValue);
        } else if (envelopeValue instanceof JSONObject) {
            JSONObject envelopeObject = (JSONObject) envelopeValue;

            Envelope envelope = new Envelope();
            envelope.attackTime = envelopeObject.optDouble("attack", envelope.attackTime);
            envelope.decayTime = envelopeObject.optDouble("decay", envelope.decayTime);
            envelope.releaseTime = envelopeObject.optDouble("release", envelope.releaseTime);
            envelope.sustainAmplitude = envelopeObject.optDouble("sustainAmplitude", envelope.sustainAmplitude);
            envelope.peekAmplitude = envelopeObject.optDouble("peekAmplitude", envelope.peekAmplitude);
            return envelope;
        }

        return new Envelope();
    }

    private SampleWave loadSampleWave(JSONObject waveObject)
            throws FileNotFoundException, IOException, UnsupportedAudioFileException {
        final String sampleSourcePath = waveObject.getString("sampleSource");

        final double[] sample;

        if (sampleSourcePath.startsWith("internal:/")) {
            final String sourcePath = sampleSourcePath.replaceFirst("internal:/", "");
            sample = SampleLoader.loadSample(InstrumentReader.class.getClassLoader().getResourceAsStream(sourcePath));
        } else {
            sample = SampleLoader.loadSample(new FileInputStream(sampleSourcePath));
        }

        int loopSampleStart = 0;
        int loopSampleEnd = sample.length - 1;

        if (waveObject.has("loop")) {
            JSONObject loopObject = waveObject.getJSONObject("loop");
            loopSampleStart = loopObject.getInt("sampleStart");
            loopSampleEnd = loopObject.getInt("sampleEnd");
        }

        SampleWave waveForm = new SampleWave(sample, loopSampleStart, loopSampleEnd);

        if (waveObject.has("amplitude"))
            waveForm.setAmplitude(waveObject.getDouble("amplitude"));

        return waveForm;
    }

    private ModulatedWave loadModulatedWave(JSONObject waveObject)
            throws FileNotFoundException, IOException, UnsupportedAudioFileException {
        Object messageWave = waveObject.get("messageWave");
        Object modulatingWave = waveObject.get("modulatingWave");

        ModulatedWave waveForm = new ModulatedWave(
                loadWaveFromJson(messageWave),
                loadWaveFromJson(modulatingWave));

        loadSimpleWaveAttributes(waveObject, waveForm);

        return waveForm;
    }

    private AdditiveWave loadAdditiveWave(JSONObject waveObject)
            throws FileNotFoundException, IOException, UnsupportedAudioFileException {
        Object bottomWave = waveObject.get("bottomWave");
        Object topWave = waveObject.get("topWave");

        AdditiveWave waveForm = new AdditiveWave(
                loadWaveFromJson(bottomWave),
                loadWaveFromJson(topWave));

        if (waveObject.has("amplitude"))
            waveForm.setAmplitude(waveObject.getDouble("amplitude"));

        return waveForm;
    }

    private WaveForm loadSimpleWave(JSONObject waveObject, WaveForm simpleWave) {
        loadSimpleWaveAttributes(waveObject, simpleWave);
        return simpleWave;
    }

    private void loadSimpleWaveAttributes(JSONObject waveObject, WaveForm wave) {
        if (waveObject.has("frequency"))
            wave.setFrequency(waveObject.getDouble("frequency"));
        if (waveObject.has("amplitude"))
            wave.setAmplitude(waveObject.getDouble("amplitude"));
    }

    private static JSONObject readJsonObjectFromStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String inputStr;
        while ((inputStr = reader.readLine()) != null)
            builder.append(inputStr);

        return new JSONObject(builder.toString());
    }

    private enum WaveType {
        TypeSine("sine"),
        TypeSaw("saw"),
        TypeSquare("square"),
        TypeTriangle("triangle"),
        TypeAdditive("additive"),
        TypeSample("sample"),
        TypeModulated("modulated");

        private String value;

        private WaveType(String s) {
            value = s;
        }

        public static WaveType GetType(String typeString) {
            for (WaveType type : WaveType.values()) {
                if (type.value.equals(typeString)) {
                    return type;
                }
            }
            return null;
        }
    }
}
