/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick;

import brick.gui.BrickFrame;
import brick.math.Brick;
import java.io.File;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * @author Szymon
 */
public class Main {
	static {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e2) {
			}
		}
	}

	static public void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BrickFrame().setVisible(true);
            }
        });
	}

	static public void main2(String[] args) {
		String path = "";
		if (args.length >= 2) {
			path = args[1];
		}
		File rootDir = new File(path);
		Brick brick = null;

		do {
			if (rootDir.isDirectory() && rootDir.canRead()) {
				int[] subdirs = {0, 1, 2, 3, 4, 5};
				for (int a : subdirs) {
					
				}
			}
		} while (brick != null);
	}
}
