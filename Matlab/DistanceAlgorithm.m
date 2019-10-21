% Author: Jiøí Richter
% Date: 20.1.2015

% read source files
xFWhole = audioread('bs50d_obarveny.wav');
%xFWhole = audioread('bs50d_neobarveny.wav');

% read recorded sounds
yF = audioread('bs50d_obarveny1m1vol15.wav');
%yF = audioread('bs50d_neobarveny1m1vol15.wav');

% nonzero part of x 
%xF = xFWhole(4800:5040);
xF = xFWhole;
figure(1);
plot(xFWhole);
title('5 ms bily sum - vyslany signal (40x + 20ms ticha mezi) - 0');

% plot of whole recorded sound
figure(2);
plot(yF);
title('recorded sound');

% %--------- cross-correlation of source and recorded ----------
% figure(3);
% xFyF_cor = xcorr(xF,yF);
% plot(xFyF_cor);
% title('cross-corelation - vytvoreny signal X nahrany signal - 1FINAL');
% 
%--------- separate source sound into pieces --------------
sourceStart = 4801;
sourceStop = 5040;
sourcePieces = xF(sourceStart:sourceStop);
step = 1200;
% ziskani vsech nenulovych soucasti
for i=2:50
    sourceStart = sourceStart+step;
    sourceStop = sourceStop+step;
    sourcePieces(:,i) = xF(sourceStart:sourceStop);
end
%--------- separate recorded sound into pieces --------------
% find first bigger than threshold
thresholdMax = 0.3;
thresholdMin = 0.1;
ix = find(yF > thresholdMax,1);

% cut into smaller pieces
% indexes for separate piece
start = ix-144;
stop = ix+1056;
index = 1;

pieces = (yF(start:stop));
while (1)
   % calculate new values
   start = stop;
   stop = stop+1200;
   index = index+1;
   % check if we are at the end
   if start >= length(yF)
       break
   end
   % check if we are still in array
   if stop > length(yF);
       stop = length(yF);
   end
   % create piece
   onePiece = yF(start:stop);
   % test if there is there is any value bigger than min
   if max(onePiece) > thresholdMin
      % put piece into array
      pieces(:,index) = onePiece; 
   end   
end

% print pieces
figure(4);
plot(pieces);
title('recorded pieces of sound x - 2/3');

%-------- correlation of every piece - calculate average ---------
xcorrPieces = xcorr(sourcePieces(:,1),pieces(:,1));
numberOfPieces = size(pieces);
numberOfPieces = numberOfPieces(2);

for i=2:numberOfPieces
    xcorrPieces(:,i) = xcorr(sourcePieces(:,i),pieces(:,i));
end

figure(5);
plot(xcorrPieces);
title('cross-corelation of recorded pieces of sound x - 2');

% calculate average of every point in cross-correlation
xcorrAverage = 0;
for i=1:length(xcorrPieces)
    % calculate second size
    secDim = size(xcorrPieces);
    secDim = secDim(2);
    % suma
    sum = 0;
    for j=1:secDim
        sum = sum + xcorrPieces(i,j);
    end
    % create new xcorr
    xcorrAverage(i) = sum/secDim;  
end
figure(6);
plot(xcorrAverage);
title('average cross-corelation of recorded pieces of sound x - 2FINAL');

