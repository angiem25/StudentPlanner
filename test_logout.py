#!/usr/bin/env python3

import os
import shutil
import sys

def test_logout_persistence():
    """Test that logout preserves profile data"""
    
    # Define paths
    profiles_dir = "planner_data/profiles"
    course_file = os.path.join(profiles_dir, "muadiib24_Paul_Atreides_courses.csv")
    
    # Create test course data
    test_course_data = """550e8400-e29b-41d4-a716-446655440000,Calculus III,MATH 301,Dr. Smith,4
550e8400-e29b-41d4-a716-446655440001,Physics II,PHYS 202,Dr. Johnson,4
550e8400-e29b-41d4-a716-446655440002,Computer Science,CS 101,Dr. Brown,3"""
    
    print("=== Testing Logout Data Persistence ===")
    
    # Step 1: Create test course data
    print("1. Creating test course data...")
    with open(course_file, 'w') as f:
        f.write(test_course_data)
    
    # Step 2: Verify data exists
    print("2. Verifying test data exists...")
    with open(course_file, 'r') as f:
        content = f.read()
        line_count = len([line for line in content.strip().split('\n') if line.strip()])
        print(f"   Found {line_count} courses in file")
    
    # Step 3: Run the application (this will simulate logout)
    print("3. Starting application to test logout...")
    print("   (Please manually test logout in the running application)")
    print("   Then press Enter to continue...")
    input()
    
    # Step 4: Check if data persists after logout
    print("4. Checking if data persists after logout...")
    if os.path.exists(course_file):
        with open(course_file, 'r') as f:
            content = f.read()
            if content.strip():
                line_count = len([line for line in content.strip().split('\n') if line.strip()])
                print(f"   SUCCESS: Found {line_count} courses after logout")
                return True
            else:
                print("   FAILED: Course file is empty after logout")
                return False
    else:
        print("   FAILED: Course file does not exist after logout")
        return False

if __name__ == "__main__":
    success = test_logout_persistence()
    sys.exit(0 if success else 1)
