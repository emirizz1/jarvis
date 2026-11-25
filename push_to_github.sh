#!/bin/bash
# TELEFONDAN GITHUB'A PUSH - Termux'ta calistir

# 1. GitHub'da bos bir repo olusturdun mu? Olusturmadiysan once onu yap:
# github.com > New repository > isim: jarvis-app > Public > Create

echo "GitHub kullanıcı adın ne? (örn: ahmet123)"
read USERNAME
echo "Repo ismi ne? (default: jarvis-app)"
read REPO
if [ -z "$REPO" ]; then REPO="jarvis-app"; fi

cd /home/user/jarvis-app

git init
git config user.email "jarvis@phone.local"
git config user.name "Jarvis"
git add .
git commit -m "Jarvis v3 - ogrenen AI"
git branch -M main
git remote remove origin 2>/dev/null; true
git remote add origin https://github.com/$USERNAME/$REPO.git

echo ""
echo "Simdi GitHub PAT token lazim:"
echo "github.com > Settings > Developer settings > Personal access tokens > Tokens (classic) > Generate new token"
echo "Scope: repo sec, generate de, tokeni kopyala"
echo ""
echo "Tokeni buraya yapistir:"
read TOKEN

git push -u origin main -f --verbose <<EOF
username: $USERNAME
password: $TOKEN
EOF

# alternatif push (token URL'e gomulu)
# git push https://$TOKEN@github.com/$USERNAME/$REPO.git main

echo ""
echo "Push bitti! Simdi github.com/$USERNAME/$REPO > Actions'a gir, build'i izle."
