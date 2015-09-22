#!/bin/bash
#
# watchdog
#
# Run as a cron job to keep an eye on what_to_monitor which should always
# be running. Restart what_to_monitor and send notification as needed.
#
# This needs to be run as root or a user that can start system services.
#
# Revisions: 0.1 (20100506), 0.2 (20100507)

# 9001 - port of MBB process
# 9002 - port of MBB Image process
NAME1=9001
NAME2=9002

TO1=mybabybox.hk@gmail.com
TO2=keithlei01@gmail.com

GREP=/bin/grep
PS=/bin/ps
NOP=/bin/true
DATE=/bin/date
MAIL=/usr/sbin/ssmtp
RM=/bin/rm

$PS -ef|$GREP -v grep|$GREP java|$GREP $NAME1 >/dev/null 2>&1
case "$?" in
 1)
 echo "MBB $NAME1 is NOT RUNNING. Sending notices."
 NOTICE=/tmp/watchdog_$NAME1.txt
 echo -e "To: $TO1\nFrom: $TO1\nSubject: MBB PROD Alert - $NAME1 was not running as of `$DATE`" > $NOTICE
 $MAIL $TO1 < $NOTICE
 echo -e "To: $TO2\nFrom: $TO2\nSubject: MBB PROD Alert - $NAME1 was not running as of `$DATE`" > $NOTICE
 $MAIL $TO2 < $NOTICE
 # $RM -f $NOTICE
 ;;
esac

$PS -ef|$GREP -v grep|$GREP java|$GREP $NAME2 >/dev/null 2>&1
case "$?" in
 1)
 echo "MBB $NAME2 is NOT RUNNING. Sending notices."
 NOTICE=/tmp/watchdog_$NAME2.txt
 echo -e "To: $TO1\nFrom: $TO1\nSubject: MBB PROD Alert - $NAME2 was not running as of `$DATE`" > $NOTICE
 $MAIL $TO1 < $NOTICE
 echo -e "To: $TO2\nFrom: $TO2\nSubject: MBB PROD Alert - $NAME2 was not running as of `$DATE`" > $NOTICE
 $MAIL $TO2 < $NOTICE
 # $RM -f $NOTICE
 ;;
esac

exit
