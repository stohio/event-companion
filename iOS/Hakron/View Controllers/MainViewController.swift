//
//  MainViewController.swift
//  Hakron
//
//  Created by Aron Gates on 1/9/17.
//  Copyright © 2017 Aron Gates. All rights reserved.
//

import SideMenu
import UIKit

class MainViewController: MenuItem {
        
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func encodeRestorableState(with coder: NSCoder) {
        
        if let menuNumber = menuNumber {
            coder.encode(menuNumber, forKey: "0")
        }
        
        super.encodeRestorableState(with: coder)
    }
    
    override func decodeRestorableState(with coder: NSCoder) {
        menuNumber = coder.decodeInteger(forKey: "0")
        
        super.decodeRestorableState(with: coder)
    }
    
    override func applicationFinishedRestoringState() {
        guard menuNumber != nil else { return }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}
