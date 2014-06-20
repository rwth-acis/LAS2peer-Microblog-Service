cd %~dp0
cd ..
set BASE=%CD%
set CLASSPATH="%BASE%/lib/*;%BASE%/service/*;"

java -cp %CLASSPATH% i5.las2peer.tools.L2pNodeLauncher windows_shell -s 9011 - uploadStartupDirectory startService('i5.las2peer.services.microblogService.MicroblogService') startWebConnector interactive
pause
