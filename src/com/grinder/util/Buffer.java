package com.grinder.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public final class Buffer  {

	private static final char[] cp1252AsciiExtension = new char[]{'€', '\u0000', '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '\u0000', 'Ž', '\u0000', '\u0000', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '\u0000', 'ž', 'Ÿ'};
	private static final int[] BIT_MASKS = {0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff,
			0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
			0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1};
	private static final BigInteger RSA_MODULUS = new BigInteger("65672264916231626826998354696249025278521758435867349511182700702097499052981337016732629372849876242108149324018633435138653830239163152031469190272457385401153536874226456824492653627441835732900225277736647133156098611405349076508448895319565223151344494462635655056096355457788931055172717380593008751680552212094445271484699550651361905107023580353026181062008345867473464481393479838844433982265843906048041569992095650576691544730131302292490367777002784368387579025616686753942130928038783840250157389812999606608016152812377430991617727744903231176930422163487331625243430030245758552240707453");
	private static final BigInteger RSA_EXPONENT = new BigInteger("65537");
	public byte array[];
	public int index;
	public int bitPosition;

	public Buffer(byte[] array) {
		this.array = array;
		index = 0;
	}

	public Buffer(int var1) {
		this.array = new byte[var1];
		this.index = 0;
	}

	public final int readUTriByte() {
		index += 3;
		return (0xff & array[index - 3] << 16) + (0xff & array[index - 2] << 8)
				+ (0xff & array[index - 1]);
	}

	public final int readUTriByte(int i) {
		index += 3;
		return (0xff & array[index - 3] << 16) + (0xff & array[index - 2] << 8)
				+ (0xff & array[index - 1]);
	}

	public int readUSmart2() {
		int baseVal = 0;
		int lastVal = 0;
		while ((lastVal = readUSmart()) == 32767) {
			baseVal += 32767;
		}
		return baseVal + lastVal;
	}

	public String readNewString() {
		int i = index;
		while (array[index++] != 0)
			;
		return new String(array, i, index - i - 1);
	}

	public void writeByte(int value) {
		array[index++] = (byte) value;
	}

	public void writeShort(int value) {
		array[index++] = (byte) (value >> 8);
		array[index++] = (byte) value;
	}

	public void writeTriByte(int value) {
		array[index++] = (byte) (value >> 16);
		array[index++] = (byte) (value >> 8);
		array[index++] = (byte) value;
	}

	public void writeInt(int value) {
		array[index++] = (byte) (value >> 24);
		array[index++] = (byte) (value >> 16);
		array[index++] = (byte) (value >> 8);
		array[index++] = (byte) value;
	}

	public void writeLEInt(int value) {
		array[index++] = (byte) value;
		array[index++] = (byte) (value >> 8);
		array[index++] = (byte) (value >> 16);
		array[index++] = (byte) (value >> 24);
	}

	public void writeLong(long value) {
		try {
			array[index++] = (byte) (int) (value >> 56);
			array[index++] = (byte) (int) (value >> 48);
			array[index++] = (byte) (int) (value >> 40);
			array[index++] = (byte) (int) (value >> 32);
			array[index++] = (byte) (int) (value >> 24);
			array[index++] = (byte) (int) (value >> 16);
			array[index++] = (byte) (int) (value >> 8);
			array[index++] = (byte) (int) value;
		} catch (RuntimeException runtimeexception) {
			throw new RuntimeException();
		}
	}

	public void writeString(String text) {
		System.arraycopy(text.getBytes(), 0, array, index, text.length());
		index += text.length();
		array[index++] = 10;
	}

	public void writeBytes(byte data[], int offset, int length) {
		for (int index = length; index < length + offset; index++)
			array[this.index++] = data[index];
	}

	public void writeBytes(byte data[]) {
		for (byte b : data) {
			writeByte(b);
		}
	}

	public void writeBytes(int value) {
		array[index - value - 1] = (byte) value;
	}

	public int method440() {
		index += 4;
		return ((array[index - 3] & 0xFF) << 24) + ((array[index - 4] & 0xFF) << 16)
				+ ((array[index - 1] & 0xFF) << 8) + (array[-2] & 0xFF);
	}

	public int readUnsignedByte() {
		return array[index++] & 0xff;
	}

	public int getUnsignedByte() {
		return readUnsignedByte();
	}

	public int readShort2() {
		index += 2;
		int i = ((array[index - 2] & 0xff) << 8) + (array[index - 1] & 0xff);
		if (i > 32767)
			i -= 65537;
		return i;
	}

	public byte readSignedByte() {
		return array[index++];
	}

	public int read24Int() {
		index += 3;
		return ((array[index - 3] & 0xff) << 16) + ((array[index - 2] & 0xff) << 8) + (array[index - 1] & 0xff);
	}

	public int readUShort() {
		index += 2;
		return ((array[index - 2] & 0xff) << 8)
				+ (array[index - 1] & 0xff);
	}

	public int readShort() {
		index += 2;
		int value = ((array[index - 2] & 0xff) << 8) + (array[index - 1] & 0xff);

		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int readTriByte() {
		index += 3;
		return ((array[index - 3] & 0xff) << 16) + ((array[index - 2] & 0xff) << 8)
				+ (array[index - 1] & 0xff);
	}

	public int readInt() {
		index += 4;
		return ((array[index - 4] & 0xff) << 24) + ((array[index - 3] & 0xff) << 16)
				+ ((array[index - 2] & 0xff) << 8) + (array[index - 1] & 0xff);
	}

	public long readLong() {
		long msi = (long) readInt() & 0xffffffffL;
		long lsi = (long) readInt() & 0xffffffffL;
		return (msi << 32) + lsi;
	}

	public String readString() {
		int index = this.index;
		while (array[this.index++] != 10)
			;
		return new String(array, index, this.index - index - 1, StandardCharsets.UTF_8);
	}

	public String readStringCp1252NullTerminated() {
		int var1 = this.index;

		while (this.array[++this.index - 1] != 0) {
		}

		int var2 = this.index - var1 - 1;
		return var2 == 0 ? "" : decodeStringCp1252(this.array, var1, var2);
	}

	private static String decodeStringCp1252(byte[] var0, int var1, int var2) {
		char[] var3 = new char[var2];
		int var4 = 0;

		for (int var5 = 0; var5 < var2; ++var5) {
			int var6 = var0[var5 + var1] & 255;
			if (var6 != 0) {
				if (var6 >= 128 && var6 < 160) {
					char var7 = cp1252AsciiExtension[var6 - 128];
					if (var7 == 0) {
						var7 = '?';
					}

					var6 = var7;
				}

				var3[var4++] = (char)var6;
			}
		}

		return new String(var3, 0, var4);
	}

	public byte[] readBytes() {
		int index = this.index;
		while (array[this.index++] != 10)
			;
		byte data[] = new byte[this.index - index - 1];
		System.arraycopy(array, index, data, index - index, this.index - 1 - index);
		return data;
	}

	public void readBytes(int offset, int length, byte data[]) {
		for (int index = length; index < length + offset; index++)
			data[index] = array[this.index++];
	}

	public void initBitAccess() {
		bitPosition = index * 8;
	}

	public int readBits(int amount) {
		int byteOffset = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		int value = 0;
		bitPosition += amount;
		for (; amount > bitOffset; bitOffset = 8) {
			value += (array[byteOffset++] & BIT_MASKS[bitOffset]) << amount - bitOffset;
			amount -= bitOffset;
		}
		if (amount == bitOffset)
			value += array[byteOffset] & BIT_MASKS[bitOffset];
		else
			value += array[byteOffset] >> bitOffset - amount & BIT_MASKS[amount];
		return value;
	}

	public void disableBitAccess() {
		index = (bitPosition + 7) / 8;
	}

	public int readSmart() {
		int value = array[index] & 0xff;
		if (value < 128)
			return readUnsignedByte() - 64;
		else
			return readUShort() - 49152;
	}

	public int getSmart() {
		try {
			// checks current without modifying position
			if (index >= array.length) {
				return array[array.length - 1] & 0xFF;
			}
			int value = array[index] & 0xFF;

			if (value < 128) {
				return readUnsignedByte();
			} else {
				return readUShort() - 32768;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return readUShort() - 32768;
		}
	}

	public int readUSmart() {
		int value = array[index] & 0xff;
		if (value < 128)
			return readUnsignedByte();
		else
			return readUShort() - 32768;
	}

	public byte peek()
	{
		return array[index];
	}

	public int readUnsignedShortSmart()
	{
		int peek = this.peek() & 0xFF;
		return peek < 128 ? this.readUnsignedByte() : this.readUShort() - 0x8000;
	}

	public int readUnsignedIntSmartShortCompat()
	{
		int var1 = 0;

		int var2;
		for (var2 = this.readUnsignedShortSmart(); var2 == 32767; var2 = this.readUnsignedShortSmart())
		{
			var1 += 32767;
		}

		var1 += var2;
		return var1;
	}
	public void writeNegatedByte(int value) {
		array[index++] = (byte) (-value);
	}

	public void writeByteS(int value) {
		array[index++] = (byte) (128 - value);
	}

	public int readUByteA() {
		return array[index++] - 128 & 0xff;
	}

	public int readNegUByte() {
		return -array[index++] & 0xff;
	}

	public int readUByteS() {
		return 128 - array[index++] & 0xff;
	}

	public byte readNegByte() {
		return (byte) -array[index++];
	}

	public int getUnsignedByteC() {
		return -array[index++] & 0xff;
	}

	public byte readByteS() {
		return (byte) (128 - array[index++]);
	}

	public void writeLEShort(int value) {
		array[index++] = (byte) value;
		array[index++] = (byte) (value >> 8);
	}

	public void writeShortA(int value) {
		array[index++] = (byte) (value >> 8);
		array[index++] = (byte) (value + 128);
	}

	public void writeLEShortA(int value) {
		array[index++] = (byte) (value + 128);
		array[index++] = (byte) (value >> 8);
	}

	public int readLEUShort() {
		index += 2;
		return ((array[index - 1] & 0xff) << 8)
				+ (array[index - 2] & 0xff);
	}

	public int readUShortA() {
		index += 2;
		return ((array[index - 2] & 0xff) << 8) + (array[index - 1] - 128 & 0xff);
	}

	public int readLEUShortA() {
		index += 2;
		return ((array[index - 1] & 0xff) << 8) + (array[index - 2] - 128 & 0xff);
	}

	public int readLEShort() {
		index += 2;
		int value = ((array[index - 1] & 0xff) << 8) + (array[index - 2] & 0xff);

		if (value > 32767) {
			value -= 0x10000;
		}
		return value;
	}

	public int readLEShortA() {
		index += 2;
		int value = ((array[index - 1] & 0xff) << 8) + (array[index - 2] - 128 & 0xff);
		if (value > 32767)
			value -= 0x10000;
		return value;
	}

	public int getIntLittleEndian() {
		index += 4;
		return ((array[index - 4] & 0xFF) << 24) + ((array[index - 3] & 0xFF) << 16)
				+ ((array[index - 2] & 0xFF) << 8) + (array[index - 1] & 0xFF);
	}

	public int readMEInt() { // V1
		index += 4;
		return ((array[index - 2] & 0xff) << 24) + ((array[index - 1] & 0xff) << 16)
				+ ((array[index - 4] & 0xff) << 8) + (array[index - 3] & 0xff);
	}

	public int readIMEInt() { // V2
		index += 4;
		return ((array[index - 3] & 0xff) << 24) + ((array[index - 4] & 0xff) << 16)
				+ ((array[index - 1] & 0xff) << 8) + (array[index - 2] & 0xff);
	}

	public void writeReverseDataA(byte data[], int length, int offset) {
		for (int index = (length + offset) - 1; index >= length; index--) {
			array[this.index++] = (byte) (data[index] + 128);
		}
	}

	public void readReverseData(byte data[], int offset, int length) {
		for (int index = (length + offset) - 1; index >= length; index--) {
			data[index] = array[this.index++];
		}
	}

	public void getBytes(int len, int off, byte[] dest) {
		for (int i = off; i < off + len; i++) {
			dest[i] = array[index++];
		}
	}
	public void getBytes2(int startPos, int endPos, byte buf[]) {
		for (int k = (endPos + startPos) - 1; k >= endPos; k--)
			buf[k] = array[index++];
	}
	public void copyArray(byte[] array, int offset, int length) {
		for(int i = offset; i < length + offset; ++i) {
			array[i] = this.array[++this.index - 1];
		}
	}
	public void encryptRSAContent() {
		/* Cache the current position for future use */
		int cachedPosition = index;

		/* Reset the position */
		index = 0;

		/*
		 * An empty byte array with a capacity of {@code #currentPosition} bytes
		 */
		byte[] decodeBuffer = new byte[cachedPosition];

		/*
		 * Gets bytes up to the current position from the buffer and populates
		 * the {@code #decodeBuffer}
		 */
		getBytes(cachedPosition, 0, decodeBuffer);

		/*
		 * The decoded big integer which translates the {@code #decodeBuffer}
		 * into a {@link BigInteger}
		 */
		BigInteger decodedBigInteger = new BigInteger(decodeBuffer);

		/*
		 * This is going to be a mouthful... the encoded {@link BigInteger} is
		 * responsible of returning a value which is the value of {@code
		 * #decodedBigInteger}^{@link #RSA_EXPONENT} mod (Modular arithmetic can
		 * be handled mathematically by introducing a congruence relation on the
		 * integers that is compatible with the operations of the ring of
		 * integers: addition, subtraction, and multiplication. For a positive
		 * integer n, two integers a and b are said to be congruent modulo n)
		 * {@link #RSA_MODULES}
		 */
		BigInteger encodedBigInteger = decodedBigInteger.modPow(RSA_EXPONENT, RSA_MODULUS);

		/*
		 * Returns the value of the {@code #encodedBigInteger} translated to a
		 * byte array in big-endian byte-order
		 */
		byte[] encodedBuffer = encodedBigInteger.toByteArray();

		/* Reset the position so we can write fresh to the buffer */
		index = 0;

		/*
		 * We put the length of the {@code #encodedBuffer} to the buffer as a
		 * standard byte. (Ignore the naming, that really writes a byte...)
		 */
		writeByte(encodedBuffer.length);

		/* Put the bytes of the {@code #encodedBuffer} into the buffer. */
		writeBytes(encodedBuffer, encodedBuffer.length, 0);
	}
	public int getUnsignedLEShort() {
		this.index += 2;
		return (this.array[this.index - 1] & 255)
				+ ((this.array[this.index - 2] & 255) << 8);
	}
	public int readByteOrShort1() {
		int var1 = this.array[this.index] & 255;
		return var1 < 128
				?this.readUnsignedByte() - 64
				:this.getUnsignedLEShort() - 49152;
	}
	public int readByteOrShort2() {
		int var1 = this.array[this.index] & 255;
		return var1 < 128?this.readUnsignedByte():this.getUnsignedLEShort() - 32768;
	}
	public int readIntOrShort() {
		return this.array[this.index] < 0
				?this.readInt() & Integer.MAX_VALUE
				:this.getUnsignedLEShort();
	}
	public void writeMedium(int var1) {
		this.array[++this.index - 1] = (byte)(var1 >> 16);
		this.array[++this.index - 1] = (byte)(var1 >> 8);
		this.array[++this.index - 1] = (byte)var1;
	}
	public int readMedium() {
		this.index += 3;
		return ((this.array[this.index - 3] & 255) << 16)
				+ (this.array[this.index - 1] & 255)
				+ ((this.array[this.index - 2] & 255) << 8);
	}
	public void __t_298(int var1) {
		this.array[this.index - var1 - 4] = (byte)(var1 >> 24);
		this.array[this.index - var1 - 3] = (byte)(var1 >> 16);
		this.array[this.index - var1 - 2] = (byte)(var1 >> 8);
		this.array[this.index - var1 - 1] = (byte)var1;
	}
	public void writeSmartValue(int var1) {
		if((var1 & -128) != 0) {
			if((var1 & -16384) != 0) {
				if((var1 & -2097152) != 0) {
					if((var1 & -268435456) != 0) {
						this.writeByte(var1 >>> 28 | 128);
					}

					this.writeByte(var1 >>> 21 | 128);
				}

				this.writeByte(var1 >>> 14 | 128);
			}

			this.writeByte(var1 >>> 7 | 128);
		}

		this.writeByte(var1 & 127);
	}
	public void writeSmartByteShort(int var1) {
		if(var1 >= 0 && var1 < 128) {
			this.writeByte(var1);
		} else if(var1 >= 0 && var1 < 32768) {
			this.writeShort(var1 + 32768);
		} else {
			throw new IllegalArgumentException();
		}
	}
	public int __as_311() {
		byte var1 = this.array[++this.index - 1];

		int var2;
		for(var2 = 0; var1 < 0; var1 = this.array[++this.index - 1]) {
			var2 = (var2 | var1 & 127) << 7;
		}

		return var2 | var1;
	}

	public void xteaDecrypt(int[] var1, int var2, int var3) {
		int var4 = this.index;
		this.index = var2;
		int var5 = (var3 - var2) / 8;

		for(int var6 = 0; var6 < var5; ++var6) {
			int var7 = this.readInt();
			int var8 = this.readInt();
			int var10 = -957401312;
			int var11 = -1640531527;

			for(int var12 = 32; var12-- > 0; var8 -= var8 + (var8 << 4 ^ var8 >>> 5) ^ var10 + var1[var10 & 3]) {
				var8 -= var8 + (var8 << 4 ^ var8 >>> 5) ^ var1[var10 >>> 11 & 3] + var10;
				var10 -= var11;
			}


			this.index -= 8;
			this.writeInt(var7);
			this.writeInt(var8);
		}

		this.index = var4;
	}

	public byte readByte() {
		return this.array[++this.index - 1];
	}

	public void writeCharSequence(CharSequence sequence) {
		int var3 = sequence.length();
		int value = 0;

		for(int var5 = 0; var5 < var3; ++var5) {
			char var6 = sequence.charAt(var5);
			if(var6 <= 127) {
				++value;
			} else if(var6 <= 2047) {
				value += 2;
			} else {
				value += 3;
			}
		}

		this.array[++this.index - 1] = 0;
		this.writeSmartValue(value);
		this.index += encodeSequence(this.array, this.index, sequence);
	}

	public String __aw_304() {
		byte var1 = this.array[++this.index - 1];
		if(var1 != 0) {
			throw new IllegalStateException("");
		} else {
			int var2 = this.__as_311();
			if(var2 + this.index > this.array.length) {
				throw new IllegalStateException("");
			} else {
				byte[] var4 = this.array;
				int var5 = this.index;
				char[] var6 = new char[var2];
				int var7 = 0;
				int var8 = var5;

				int var11;
				for(int var9 = var5 + var2; var8 < var9; var6[var7++] = (char)var11) {
					int var10 = var4[var8++] & 255;
					if(var10 < 128) {
						if(var10 == 0) {
							var11 = 65533;
						} else {
							var11 = var10;
						}
					} else if(var10 < 192) {
						var11 = 65533;
					} else if(var10 < 224) {
						if(var8 < var9 && (var4[var8] & 192) == 128) {
							var11 = (var10 & 31) << 6 | var4[var8++] & 63;
							if(var11 < 128) {
								var11 = 65533;
							}
						} else {
							var11 = 65533;
						}
					} else if(var10 < 240) {
						if(var8 + 1 < var9 && (var4[var8] & 192) == 128 && (var4[var8 + 1] & 192) == 128) {
							var11 = (var10 & 15) << 12 | (var4[var8++] & 63) << 6 | var4[var8++] & 63;
							if(var11 < 2048) {
								var11 = 65533;
							}
						} else {
							var11 = 65533;
						}
					} else if(var10 < 248) {
						if(var8 + 2 < var9 && (var4[var8] & 192) == 128 && (var4[var8 + 1] & 192) == 128 && (var4[var8 + 2] & 192) == 128) {
							var11 = (var10 & 7) << 18 | (var4[var8++] & 63) << 12 | (var4[var8++] & 63) << 6 | var4[var8++] & 63;
							if(var11 >= 65536 && var11 <= 1114111) {
								var11 = 65533;
							} else {
								var11 = 65533;
							}
						} else {
							var11 = 65533;
						}
					} else {
						var11 = 65533;
					}
				}

				String var3 = new String(var6, 0, var7);
				this.index += var2;
				return var3;
			}
		}
	}
	public int __aq_303() {
		this.index += 2;
		int var1 = (this.array[this.index - 1] & 255) + ((this.array[this.index - 2] & 255) << 8);
		if(var1 > 32767) {
			var1 -= 65536;
		}

		return var1;
	}

	public int __bq_318() {
		return 128 - this.array[++this.index - 1] & 255;
	}
	public int __bb_317() {
		return this.array[++this.index - 1] - 128 & 255;
	}
	public int __bm_326() {
		this.index += 2;
		return ((this.array[this.index - 1] & 255) << 8) + (this.array[this.index - 2] - 128 & 255);
	}

	private static int encodeSequence(byte[] var0, int var1, CharSequence var2) {
		int var3 = var2.length();
		int var4 = var1;

		for(int var5 = 0; var5 < var3; ++var5) {
			char var6 = var2.charAt(var5);
			if(var6 <= 127) {
				var0[var4++] = (byte)var6;
			} else if(var6 <= 2047) {
				var0[var4++] = (byte)(192 | var6 >> 6);
				var0[var4++] = (byte)(128 | var6 & '?');
			} else {
				var0[var4++] = (byte)(224 | var6 >> '\f');
				var0[var4++] = (byte)(128 | var6 >> 6 & 63);
				var0[var4++] = (byte)(128 | var6 & '?');
			}
		}

		return var4 - var1;
	}
	public int __at_308() {
		int var1 = 0;

		int var2;
		for(var2 = this.readByteOrShort2(); var2 == 32767; var2 = this.readByteOrShort2()) {
			var1 += 32767;
		}

		var1 += var2;
		return var1;
	}

	public Map<Integer, Object> readStringIntParams(Map<Integer, Object> params) {
		int length = readUnsignedByte();

		if (params == null) {
			params = new HashMap<>();
		}

		for (int i = 0; i < length; ++i) {
			boolean isString = readUnsignedByte() == 1;
			int key = readMedium();
			Object value;
			
			if (isString) {
				value = readStringCp1252NullTerminated();
			} else {
				value = readInt();
			}

			params.put(key, value);
		}

		return params;
	}
}