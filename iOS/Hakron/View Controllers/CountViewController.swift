//
//  CountViewController.swift
//  Hakron
//
//  Created by Aron Gates on 1/9/17.
//  Copyright © 2017 Aron Gates. All rights reserved.
//

import SideMenu
import UIKit

class CountViewController: MenuItem {
    
    var countDownLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
                
        countDownLabel = UILabel(frame: CGRect(x: self.view.frame.midX - self.view.frame.width / 4, y: self.view.frame.midY, width: self.view.frame.width / 2, height: self.view.frame.height / 32))
        countDownLabel.textAlignment = NSTextAlignment.center
        self.view.addSubview(countDownLabel)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        _ = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(updateTime), userInfo: nil, repeats: true)
    }
    
    override func encodeRestorableState(with coder: NSCoder) {
        
        if let menuNumber = menuNumber {
            coder.encode(menuNumber, forKey: "1")
        }
        
        super.encodeRestorableState(with: coder)
    }
    
    override func decodeRestorableState(with coder: NSCoder) {
        menuNumber = coder.decodeInteger(forKey: "1")
        
        super.decodeRestorableState(with: coder)
    }
    
    override func applicationFinishedRestoringState() {
        guard menuNumber != nil else { return }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc fileprivate func updateTime()
    {
        let endDateString = "01-24-2017 12:00:00"
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = TimeZone.ReferenceType.local
        dateFormatter.dateFormat = "MM-dd-yyyy HH:mm:ss"
        let endDate = dateFormatter.date(from: endDateString)!
        let days = String(endDate.days(from: Date()))
        let hours = String(endDate.hours(from: Date()) % 24)
        let minutes = String(endDate.minutes(from: Date()) % (60))
        let seconds = String(endDate.seconds(from: Date()) % (60))
        let timeLeft = days + " " + hours + " " + minutes + " " + seconds
        countDownLabel.text = timeLeft
    }
    
}

extension Date {
    func days(from date: Date) -> Int {
        return Calendar.current.dateComponents([.day], from: date, to: self).day!
    }
    func hours(from date: Date) -> Int {
        return Calendar.current.dateComponents([.hour], from: date, to: self).hour!
    }
    func minutes(from date: Date) -> Int {
        return Calendar.current.dateComponents([.minute], from: date, to: self).minute!
    }
    func seconds(from date: Date) -> Int {
        return Calendar.current.dateComponents([.second], from: date, to: self).second!
    }
}
