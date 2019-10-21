% Author: Jiøí Richter
% Date: 18.1.2015

% vzorkovaci frekvence
Fs = 48000;

% 10 ms signalu + 20 ms ticha - opakovat 40x

% 5 ms chirp
n = 1:(Fs/200);
time = n * 1/Fs;
xF = chirp (time,2000,0.005,7500);
%xF = 1*cos(2*pi*4500*time);
figure(1);plot(xF);

% pouziti Hannova okna
H = hanning(length(xF));
xF = xF'.*H;
figure(2);plot(time,xF);

% add title and name of axis
xlabel('time [s]')
ylabel('Amplitude');

% cross-correlation with itself
[xF_crossCorr,lags] = xcorr(xF,xF);
corrTime = (1 : length(xF_crossCorr))*1/Fs;
figure(3);plot(lags,xF_crossCorr);
xlabel('Lag [s]');
ylabel('Correlation');

% ticho 20 ms
xz20 = zeros(1,960);
% ticho 180 ms
xz180 = zeros(1,8640);

xFF = [xz180 xz20];

% opakovani 40x
for i=1:40
    xFF = [xFF xF' xz20];
end

% pridani 180 ms ticha na konec
xFF = [xFF xz180];
    
% print
n2 = 1:length(xFF);
time2 = n2 * 1/48000;
figure(3); plot(time2,xFF);
% figure(4); plot(xcorr(xF,xFF));
% 
% % vytvoreni zvukovych souboru   
%audiowrite ('chirp50x2-7khz.wav',xFF,Fs);
%audiowrite ('cos50x_4500hz.wav',xFF,Fs);