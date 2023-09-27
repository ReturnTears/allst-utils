#!/bin/bash

# 进入文件夹，修改为你自己的
cd /path/to/folder

# 遍历文件夹中的所有文件
for file in *; do
  # 检查文件是否为普通文件
  if [[ -f $file ]]; then
    # 检查文件扩展名是否为 .txt
    if [[ $file == *.txt ]]; then
      # 修改文件名
      var=$file
      # mv "$file" "${${var^^}%.txt}.md" # 将所有的小写转换为大写
      mv "$file" "${file%.txt}.md"
    fi
  fi
done
