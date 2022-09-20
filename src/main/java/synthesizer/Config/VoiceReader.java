package synthesizer.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import synthesizer.WaveForms.AdditiveWave;
import synthesizer.WaveForms.Envelope;
import synthesizer.WaveForms.ModulatedWave;
import synthesizer.WaveForms.SawWave;
import synthesizer.WaveForms.SineWave;
import synthesizer.WaveForms.SquareWave;
import synthesizer.WaveForms.TriangleWave;
import synthesizer.WaveForms.Voice;
import synthesizer.WaveForms.WaveForm;

public class VoiceReader {

    HashMap<String, WaveForm> namedWaves = new HashMap<>();
    HashMap<String, Envelope> namedEnvelopes = new HashMap<>();

    public static Map<String, Voice> load(InputStream stream) throws IOException {
        return new VoiceReader().read(stream);
    }

    public Map<String, Voice> read(InputStream stream) throws IOException {
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

        HashMap<String, Voice> voices = new HashMap<>();

        if (rootObject.has("namedVoices")) {
            JSONObject namedVoiceObjects = rootObject.getJSONObject("namedVoices");

            for (String key : namedVoiceObjects.keySet()) {
                voices.put(key, loadVoiceFromJson(namedVoiceObjects.getJSONObject(key), namedWaves, namedEnvelopes));
            }
        }

        return voices;
    }

    private Voice loadVoiceFromJson(JSONObject voiceObject, Map<String, WaveForm> waveForms,
            Map<String, Envelope> envelopes) {

        return new Voice(
                loadWaveFromJson(voiceObject.get("waveForm")),
                loadEnvelopeFromJson(voiceObject.get("envelope")));
    }

    private WaveForm loadWaveFromJson(Object waveValue) {
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

    private ModulatedWave loadModulatedWave(JSONObject waveObject) {
        Object messageWave = waveObject.get("messageWave");
        Object modulatingWave = waveObject.get("modulatingWave");

        ModulatedWave waveForm = new ModulatedWave(
                loadWaveFromJson(messageWave),
                loadWaveFromJson(modulatingWave));

        loadSimpleWaveAttributes(waveObject, waveForm);

        return waveForm;
    }

    private AdditiveWave loadAdditiveWave(JSONObject waveObject) {
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
