//
//  MenuViewController.swift
//  Hakron
//
//  Created by Aron Gates on 1/11/17.
//  Copyright © 2017 Aron Gates. All rights reserved.
//

import SideMenu
import UIKit

class MenuViewController: UITableViewController {
    
    private let menuList = ["Home", "Countdown", "Schedule", "Mentor"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if let destination = segue.destination as? MenuItem
        {
            let indexPath = self.tableView.indexPathForSelectedRow
            destination.menuNumber = indexPath!.row
        }
    }
    
    public func returnMenuItem(id: Int) -> String
    {
        return menuList[id]
    }
    
}
