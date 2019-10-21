% Author: Jiøí Richter
% Date: 18.1.2015

% vzorkovaci frekvence
Fs = 48000;

% 20ms ms signal ticho
xz = zeros(1,960);
% ticho 180 ms
xz180 = zeros(1,8640);

% 50x 5ms bileho sumu + 1s na ustaleni filtru pred a za = 2250 ms bili sum
whiteNoise = (rand(1,2.250 * Fs)*2)-1;
figure(1);plot(whiteNoise);

% obarveni bileho sumu filtrem
% filtr od 3000 do 7500 Hz ... pass-band 200 Hz a standartne
Wp = [2000 7500]/(Fs/2); Ws = [2000-200 7500+200]/(Fs/2);
    Rp = 3; Rs = 30;
    [n,Wp] = ellipord(Wp,Ws,Rp,Rs);     % Gives mimimum order of filter      
    [b,a] = ellip(n,Rp,Rs,Wp);          % Elliptic filter design
    %freqz(b,a,512,Fs)                  % Plots the frequency response

% aplikace filtru
x = filter (b,a,whiteNoise);
figure(2);plot(x);
% ticho 5 ms
xz5 = zeros(1,240);

% vytvoreni signalu pro mereni - 200 ms ticha na zacatek
xFilter = [xz180 xz];
xNotFilter = [xz180 xz];
% parametry pro vyriznuti signalu
start = 48000;
stop = 48239;
step = 240;
% vytvoreni Hanningova okna
H = hanning(step);

% 40x 5ms bileho sumu + 20ms ticha mezi sumy
for i=1:40
    tempF = x(start:stop);
    tempF = tempF'.*H;
    tempNF = whiteNoise(start:stop);
    tempNF = tempNF'.*H;
    % zmena amplitudy signalu od -1 do 1 ????
    tempF = tempF.*(1/max(max(tempF),abs(min(tempF))));
    xFilter = [xFilter tempF' xz];
    xNotFilter = [xNotFilter tempNF' xz5];
    start = start+step;
    stop = stop+step;
end

% pridani 180 ms ticha na konec
xFilter = [xFilter xz180];
xNotFilter = [xNotFilter xz180];
    
% pridani ticha na konec
xFilter = [xFilter xz180];
xNotFilter = [xNotFilter xz180];

% plot of one white noise
n = 1:(Fs/200);
time = n * 1/Fs;

figure(3);plot(time,tempNF);
% add title and name of axis
%title('White noise wave with Hanning window');
xlabel('time [s]')
ylabel('Amplitude');
% cross-correlation with itself
[xNF_crossCorr,lagsNF] = xcorr(tempNF,tempNF);
figure(4);plot(lagsNF,xNF_crossCorr);
%title('Cross-correlation of White noise wave with itself');
xlabel('Lag [n]');
ylabel('Correlation');

figure(5);plot(time,tempF);
% add title and name of axis
%title('Filtered White noise wave 2000 Hz - 7500 Hz with Hanning window');
xlabel('time [s]')
ylabel('Amplitude');
% cross-correlation with itself
[xF_crossCorr,lagsF] = xcorr(tempF,tempF);
figure(6);plot(lagsF,xF_crossCorr);
%title('Cross-correlation of Filtered White noise wave with itself');
xlabel('Lag [n]');
ylabel('Correlation');



% figure(3);plot(xFilter);
% figure(4);plot(xcorr(xFilter,xFilter));
% figure(5);plot(xNotFilter);
% figure(6);plot(xcorr(xNotFilter,xNotFilter));
% 
% vytvorit zvukove soubory
%audiowrite ('wn50x_2-7khz.wav',xFilter,Fs);
%audiowrite ('wn50._allfreq.wav',xNotFilter,Fs);




