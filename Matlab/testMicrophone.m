% Author: Jiøí Richter
% Date: 20.1.2015

% plot recorded 3520 hz
[actual,fs2] = audioread('rec1760hz.wav');
s2 = audioplayer(b,fs2);
% calculating length of signal
%play(s2);

n= 0:(fs2);
time = n*1/fs2;
start = 52;
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
