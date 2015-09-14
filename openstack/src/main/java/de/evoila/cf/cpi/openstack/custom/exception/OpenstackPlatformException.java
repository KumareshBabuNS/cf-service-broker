/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom.exception;

/**
 * @author Johannes Hiemer.
 *
 */
public class OpenstackPlatformException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpenstackPlatformException(Exception e) {
		super(e);
	}

}
