#!/bin/bash

# 对同一目录下的*.gz文件进行解压，前缀相同的文件放入同名目录下

dir=$(pwd)
readonly dir
echo "对目录${dir}下的压缩文件进行解压缩..."

file_list=$(ls $dir)

cnt=0
for file in $file_list; do
    if [ -f $file ]; then
        if [ -z $(expr match $file '\(.*\).gz') ]; then continue; fi
        
        cnt=$(expr $cnt + 1)
        echo "正在处理第$cnt个文件[${file}]..."
        
        dir_name=${file:0:$[$(expr index $file .)-1]}
        file_name=$(expr match $file '\(.*\).gz')

        if [ ! -d $dir_name ]; then mkdir $dir_name; fi
        gzip -d $file 
        if [ $? -ne 0 ]; then
            echo "[${file}]解压失败"
        else
            mv $file_name $dir_name
        fi
    fi
done

echo "解压完成！共解压缩${cnt}个*.gz文件"
