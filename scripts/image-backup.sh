#!/bin/bash


STORAGE_PATH=/apps/BB-Image/
DATA_BACKUP_PATH=/root/backup_image


nowdate=`date +%Y%m%d_%H%M`


# tar up images
cd $STORAGE_PATH
tar -czf $DATA_BACKUP_PATH/image_backup_$nowdate.tgz storage/

# remove last recent N
cd $DATA_BACKUP_PATH
ls -rt | grep image_backup | head -n -6 | while read f; do
  rm -rf "$f"
done

echo "Done backing up images to $DATA_BACKUP_PATH/image_backup_$nowdate.tgz"