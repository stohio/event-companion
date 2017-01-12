//
//  MenuItem.swift
//  Hakron
//
//  Created by Aron Gates on 1/9/17.
//  Copyright © 2017 Aron Gates. All rights reserved.
//

import SideMenu
import UIKit

class MenuItem: UIViewController {
    
    var navBar: UINavigationBar = UINavigationBar()
    var menuNumber: Int?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let tap = UITapGestureRecognizer(target: self.view, action: #selector(UIView.endEditing(_:)))
        tap.cancelsTouchesInView = false
        self.view.addGestureRecognizer(tap)

    }
    
    override func viewWillAppear(_ animated: Bool) {
        guard menuNumber != nil else {
            return
        }
        
        initNavBar()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func initNavBar() {
        self.navBar.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: 74)
        self.view.addSubview(navBar)
        
        let menuBtn = UIButton(type: .custom)
        menuBtn.setImage(UIImage(named: "showMenu"), for: .normal)
        menuBtn.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
        menuBtn.addTarget(self, action: #selector(MenuItem.presentSideMenu), for: .touchUpInside)
        let menuBarBtn = UIBarButtonItem(customView: menuBtn)
        
        navigationItem.leftBarButtonItem = menuBarBtn

        navBar.items = [navigationItem]
        
        let menuController = SideMenuManager.menuLeftNavigationController?.topViewController as? MenuViewController
        
        navBar.topItem?.title = menuController?.returnMenuItem(id: menuNumber!)
    }
    
    @IBAction func presentSideMenu()
    {
        present(SideMenuManager.menuLeftNavigationController!, animated: true, completion: nil)
    }
    
}
