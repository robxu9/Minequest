/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.martin.bukkit.npclib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author martin
 */
public class NullSocket extends Socket {
	private InputStream is;
	private OutputStream os;
	
	public NullSocket() {
        byte[] buf = new byte[1];
        is = new ByteArrayInputStream(buf);
        os = new ByteArrayOutputStream();
	}

    @Override
    public InputStream getInputStream()
    {
        return is;
    }

    @Override
    public OutputStream getOutputStream()
    {
        return os;
    }
}
