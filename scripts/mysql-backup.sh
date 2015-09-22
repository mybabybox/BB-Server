#!/bin/bash


SCHEMA_NAME=babybox
DATA_BACKUP_PATH=/root/backup_mysql


function usage()
{
   SCRIPT=`basename $0`
   echo "Usage"
   echo $SCRIPT "<db password>"
   exit 1
}


if [ -n "$1" ]; then
   PASSWORD=$1
else
   usage
fi

nowdate=`date +%Y%m%d_%H%M`

# dump mysql
mysqldump -uroot -p${PASSWORD} babybox > ${DATA_BACKUP_PATH}/babybox.sql

# zip up backup sql file
cd ${DATA_BACKUP_PATH}
tar -czf mysql_backup_$nowdate.tgz babybox.sql

# remove sql file
rm -rf ${DATA_BACKUP_PATH}/babybox.sql

# remove all versions except most recent N
ls -rt | grep mysql_backup | head -n -6 | while read f; do
  rm -rf "$f"
done

echo "Done backing up MySQL to $DATA_BACKUP_PATH/mysql_backup_$nowdate.tgz"