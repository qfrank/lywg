package wg.pojo;

public class BuffInfo {

	public long buffId;
	public long times;
	public long rest_time;
	public long remain_times;
	public long flag;

	@Override
	public String toString() {
		return "BuffInfo [buffId=" + buffId + ", times=" + times + ", rest_time=" + rest_time + ", remain_times=" + remain_times + ", flag=" + flag + "]";
	}

}
