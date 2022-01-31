#!/bin/bash

STAGE=$1

export $(grep -v '^#' .env."$STAGE" | xargs)

echo
echo "Deploying to $STAGE..."
date
echo

scp ./dist/$MV_SRC $SSH_USER@$SSH_HOST:$SSH_DEST

OUT=$?

if [ $OUT = 0 ]; then
  echo 'Transfer successful, moving to target...'
  ssh -t $SSH_USER@$SSH_HOST sudo -s "mv $SSH_DEST/$MV_SRC $MV_DEST"
  echo 'Deploy successful'
else
  echo 'Deploy failed'
fi

date
echo
