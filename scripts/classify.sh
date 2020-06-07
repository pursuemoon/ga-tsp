#!/bin/bash

# 将TSP问题按照点的给定形式分成两类
# 1. 二维欧几里得坐标形式的放在 $dir_2d 里
# 2. 经纬度形式的放在 $dir_geo 里

dir_2d='../tsp_test/test_EUC_2D'
dir_geo='../tsp_test/test_GEO'

if [ ! -d $dir_2d ]; then mkdir $dir_2d; fi
if [ ! -d $dir_geo ]; then mkdir $dir_geo; fi

for dir in $(ls); do
    file_tsp=./${dir}/${dir}.tsp
    file_tour=./${dir}/${dir}.opt.tour
    
    cnt=0
    if [ -f $file_tsp ]; then cnt=$[$cnt+1]; fi
    if [ -f $file_tour ]; then cnt=$[$cnt+1]; fi
    if [ $cnt -ne 2 ]; then continue; fi
    
    cnt=0
    while read line; do
        cnt=$[$cnt+1]
        if [ $cnt -eq 5 ]; then
            ret=$(expr "$line" : '.*EUC_2D')
            if [ $ret -ne 0 ]; then
                cp -r $dir $dir_2d
            fi
            ret=$(expr "$line" : '.*GEO')
            if [ $ret -ne 0 ]; then
                cp -r $dir $dir_geo
            fi
            break
        fi
    done < $file_tsp
done
