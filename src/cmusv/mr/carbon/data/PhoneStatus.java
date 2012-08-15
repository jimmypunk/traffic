package cmusv.mr.carbon.data;

public class PhoneStatus {
	enum moveStatus{move,still,unknown};
	static moveStatus isMoving = moveStatus.unknown;
}
