/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2013-2-1	| chenlong 	| 	create the file                       
 */

package com.example.chen.ls4000.utils;

import java.util.Arrays;

/**
 * 
 * CRC数组处理
 * 
 * <p>
 * 类详细描述
 * </p>
 * 
 * @author chenlong
 * 
 */

public class CrcOperateUtil
{
	/**
	 * 为Byte数组添加两位CRC校验
	 * 
	 * @param buf
	 * @return
	 */
	public static byte[] setParamCRC(byte[] buf)
	{
		int MASK = 0x0001, CRCSEED = 0x0810;
		int remain = 0;
		
		byte val;
		for (int i = 0; i < buf.length; i++)
		{
			val = buf[i];
			for (int j = 0; j < 8; j++)
			{
				if (((val ^ remain) & MASK) != 0)
				{
					remain ^= CRCSEED;
					remain >>= 1;
					remain |= 0x8000;
				}
				else
				{
					remain >>= 1;
				}
				val >>= 1;
			}
		}
		
		byte[] crcByte = new byte[2];
		crcByte[0] = (byte) ((remain >> 8) & 0xff);
		crcByte[1] = (byte) (remain & 0xff);
		
		// 将新生成的byte数组添加到原数据结尾并返回
		return concatAll(buf, crcByte);
	}
	
	/**
	 * 根据起始和结束下标截取byte数组
	 * 
	 * @param bytes
	 * @param start
	 * @param end
	 * @return
	 */
	private static byte[] getBytesByindex(byte[] bytes, int start, int end)
	{
		byte[] returnBytes = new byte[end - start + 1];
		for (int i = 0; i < returnBytes.length; i++)
		{
			returnBytes[i] = bytes[start + i];
		}
		return returnBytes;
	}
	
	/**
	 * 对buf中offset以前crcLen长度的字节作crc校验，返回校验结果
	 * 
	 * @param buf
	 *            byte[]　
	 * @param offset
	 *            int
	 * @param crcLen
	 *            int　crc校验的长度
	 * @return int　crc结果
	 */
	private static int calcCRC(byte[] buf, int offset, int crcLen)
	{
		int MASK = 0x0001, CRCSEED = 0x0810;
		int start = offset;
		int end = offset + crcLen;
		int remain = 0;
		
		byte val;
		for (int i = start; i < end; i++)
		{
			val = buf[i];
			for (int j = 0; j < 8; j++)
			{
				if (((val ^ remain) & MASK) != 0)
				{
					remain ^= CRCSEED;
					remain >>= 1;
					remain |= 0x8000;
				}
				else
				{
					remain >>= 1;
				}
				val >>= 1;
			}
		}
		return remain;
	}
	
	/***
	 * CRC校验是否通过
	 * 
	 * @param srcByte
	 * @param length
	 * @return
	 */
	public static boolean isPassCRC(byte[] srcByte, int length)
	{
		// 取出除crc校验位的其他数组，进行计算，得到CRC校验结果
		int calcCRC = calcCRC(srcByte, 0, srcByte.length - length);
		
		// 取出CRC校验位，进行计算
		int receive = toInt(getBytesByindex(srcByte, srcByte.length - length, srcByte.length - 1));
		
		// 比较
		return calcCRC == receive;
	}
	
	/**
	 * 多个数组合并
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest)
	{
		int totalLength = first.length;
		for (byte[] array : rest)
		{
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest)
		{
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	/**
	 * Byte转换为Int
	 * 
	 * @param b
	 * @return
	 */
	public static int toInt(byte[] b)
	{
		return toInt(b, 0, 4);
	}
	
	/**
	 * Byte转换为Int
	 * 
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	public static int toInt(byte[] b, int off, int len)
	{
		int st = 0;
		if (off < 0)
			off = 0;
		if (len > 4)
			len = 4;
		for (int i = 0; i < len && (i + off) < b.length; i++)
		{
			st <<= 8;
			st += (int) b[i + off] & 0xff;
		}
		return st;
	}

	public long CRC16_11021(byte[] data)
	{
		//byte num = 0xff;
		//byte num2 = 0xff;

		//byte num3 = 1;
		//byte num4 = 160;
		//byte[] buffer = data;

		//for (int i = 0; i < buffer.Length; i++)
		//{
		//    //位异或运算
		//    num = (byte)(num ^ buffer[i]);

		//    for (int j = 0; j <= 7; j++)
		//    {
		//        byte num5 = num2;
		//        byte num6 = num;

		//        //位右移运算
		//        num2 = (byte)(num2 >> 1);
		//        num = (byte)(num >> 1);

		//        //位与运算
		//        if ((num5 & 1) == 1)
		//        {
		//            //位或运算
		//            num = (byte)(num | 0x80);
		//        }
		//        if ((num6 & 1) == 1)
		//        {
		//            num2 = (byte)(num2 ^ num4);
		//            num = (byte)(num ^ num3);
		//        }
		//    }
		//}
		//return new byte[] { num, num2 };

		int crc = 0;
		int len = data.length;
		int j=0;

		while (len-- != 0)
		{
			for (byte i = (byte) 0x80; i != 0; i /= 2)
			{
				crc *= 2;

				if ((crc & 0x10000) != 0)
				{
					crc ^= 0x11021;
				}

				if ((data[j] & i) != 0)
				{
					crc ^= 0x1021;
				}
			}

			j++;
		}

		return crc&0x0FFFFFFFFl;

	}
	
}
