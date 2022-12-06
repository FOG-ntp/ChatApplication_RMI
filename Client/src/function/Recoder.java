package function;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

//Hàm xử lý ghi âm
public class Recoder {

    private boolean running;
    private boolean runPlay;
    private ByteArrayOutputStream out;
    final AudioFormat format = getFormat();
    private final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    private TargetDataLine line;
    private int time;

    public Recoder() {
        try {
            //getLine mọt loại DataLine mà từ đó dữ liệu âm thanh có thể đuwojc đọc từ AudioSystem
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 44100.0f;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    //Hàm dừng việc ghi dữ liệu chung vào file
    public ByteArrayOutputStream stop() {
        try {
            Thread.sleep(1000);
            running = false;
            return out;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return null;
    }

    //Hàm xử lí ghi lại âm thanh khi bắt đầu
    public void captureAudio() {
        try {
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                @Override
                public void run() {
                    out = new ByteArrayOutputStream();
                    running = true;
                    try {
                        time = 0;
                        while (running) {
                            time++;
                            int count = line.read(buffer, 0, buffer.length);
                            if (count > 0) {
                                out.write(buffer, 0, count);
                            }
                        }
                        out.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
        }
    }

    //Hàm thực hiện chạy file Audio đã được ghi lại
    public void playAudio(byte[] audio, JProgressBar bar) {
        try {
            InputStream input = new ByteArrayInputStream(audio);
            final AudioFormat f = getFormat();
            final AudioInputStream ais = new AudioInputStream(input, f, audio.length / f.getFrameSize());
            DataLine.Info fo = new DataLine.Info(SourceDataLine.class, f);
            final SourceDataLine l = (SourceDataLine) AudioSystem.getLine(fo);
            l.open(f);
            l.start();
            Runnable runner = new Runnable() {
                int bufferSize = (int) f.getSampleRate() * f.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                @Override
                public void run() {
                    try {
                        runPlay = true;
                        int count;
                        while (runPlay && (count = ais.read(buffer, 0, buffer.length)) != -1) {
                            bar.setValue(bar.getValue() + 1);
                            if (count > 0) {
                                l.write(buffer, 0, count);
                            }
                        }
                        bar.setValue(bar.getMaximum());
                        l.drain();
                        l.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                    }
                }
            };
            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (Exception e) {
            System.err.println("Line unavailable: " + e);
        }
    }
    //Dừng đoạn audio đang chạy
    public void stopPlay() {
        runPlay = false;
    }
    //Nhận thời gian ghi âm
    public int getTime() {
        return time;
    }
    //Ghi lại thời gian ghi âm
    public void setTime(int time) {
        this.time = time;
    }
}
