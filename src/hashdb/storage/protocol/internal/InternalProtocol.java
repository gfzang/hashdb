package hashdb.storage.protocol.internal;

import hashdb.storage.entities.fields.EntryKeyLink;



/**
 * The Interface InternalProtocol.
 */
public interface InternalProtocol {

	int getFirstPosition(EntryKeyLink key);

	int getNextPosition(EntryKeyLink key, int previous);

	int getNthPosition(EntryKeyLink key, int n);

	int getFirstPosition(byte[] key);

	int getNextPosition(byte[] key, int previous);

	int getNthPosition(byte[] key, int n);


}
