#!/bin/bash

function usage()
{
   SCRIPT=`basename $0`
   echo "Usage"
   echo $SCRIPT "<UpgradeVersion>"
   exit 1
}


if [ -n "$1" ]; then
   UPGRADE_VERSION=$1
else
   usage
fi


IMG_TMP_ZIP=image_default.gz

APP_HOME=/apps/MB
FTP_HOME=/home/ftp
INSTALL_PATH=$APP_HOME/current
APP_LOG_PATH=$APP_HOME/current/logs
PRERELEASE_PATH=$FTP_HOME
LOG_BACKUP_PATH=$FTP_HOME/backup_logs

echo "Backing up application logs"
nowdate=`date +%Y%m%d_%H%M`
cp $APP_LOG_PATH/application.log $LOG_BACKUP_PATH/application.$nowdate.log
ls $APP_LOG_PATH/application.*.log | while read f; do
  cp "$f" $LOG_BACKUP_PATH
done


if [ -f $PRERELEASE_PATH/$IMG_TMP_ZIP ];
then
   echo "Copying default images"
   cp $PRERELEASE_PATH/$IMG_TMP_ZIP $APP_HOME/storage
   cd $APP_HOME/storage
   tar -xzf $IMG_TMP_ZIP
   rm $IMG_TMP_ZIP
fi

echo "Deploying new version $UPGRADE_VERSION from prerelease"
cp $PRERELEASE_PATH/$UPGRADE_VERSION.zip $APP_HOME

cd $APP_HOME
rm current
unzip $UPGRADE_VERSION.zip
rm -rf $UPGRADE_VERSION.zip

# remove all version directories except most recent four
ls -rt | grep parent-social | grep -v zip | head -n -4 | while read f; do
  rm -rf "$f"
done


echo "Upgrade current soft link"
ln -s $UPGRADE_VERSION current


echo "Fixing up permissions"
cd $INSTALL_PATH
chmod +x start

echo "Deploy Completed!"
