% Author: Jiøí Richter
% Date: 20.1.2015

% plot recorded 3520 hz
[b,fs2] = audioread('playedByLoudspeaker.wav');
s2 = audioplayer(b,fs2);
% calculating length of signal
%play(s2);
% higher the values of signal to be from 1 to -1
b = b*10;


n96 = 1:length(b);
time96 = n96 * 1/96000;

figure(1); plot(b);

% get sine wave for 55 Hz
sine55hz = b(100000:150000);
% figure(3); plot(sine220hz);

% get sine wave for 110 Hz
sine110hz = b(25000:350000);
% figure(3); plot(sine220hz);

% get sine wave for 220 Hz
sine220hz = b(400000:550000);
% figure(3); plot(sine220hz);

% get sine wave for 440 Hz
sine440hz = b(600000:720000);
% figure(3); plot(sine220hz);

% get sine wave for 880 Hz
sine880hz = b(800000:920000);
% figure(3); plot(sine220hz);

% get sine wave for 1760 Hz
sine1760hz = b(1000000:1100000);

% get sine wave for 3520 Hz
sine3520hz = b(1150000:1250000);
%figure(2); plot(sine3520hz);

% get sine wave for 7040 Hz
sine7040hz = b(1350000:1450000);

% get sine wave for 14080 Hz
sine14080hz = b(1550000:1650000);

% get sine wave for 19500 Hz
sine19500hz = b(1750000:1850000);

actual = sine1760hz;
% take only 20 periods
n= 0:(fs2/320);
time = n*1/fs2;
start = 52;
%b2 = sine3520hz(start:start+length(n)-1);
b2 = actual(start:start+length(n)-1);

figure(4);
plot(time,b2);

% painting spectrum for recorded sound
% 1024 samples from recorded sound
sample = actual(35000 - 5000: 35000-3977);
% fft on samples from recorded sound
FFTsample = fft (sample);
% set frequency
fs = (0:512) /1024 * 96000;
sampleFinal = FFTsample(1:513);
figure(5);
plot (fs, abs (sampleFinal));
title('spectrum');
