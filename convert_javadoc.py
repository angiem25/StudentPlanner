#!/usr/bin/env python3
"""
Convert Javadoc HTML documentation to Markdown format
"""

import os
import re
from pathlib import Path
from bs4 import BeautifulSoup
import html

def clean_text(text):
    """Clean and normalize text content"""
    if not text:
        return ""
    # Remove extra whitespace
    text = re.sub(r'\s+', ' ', text.strip())
    # Unescape HTML entities
    text = html.unescape(text)
    return text

def convert_table_to_markdown(table):
    """Convert HTML table to Markdown format"""
    if not table:
        return ""
    
    rows = table.find_all('tr')
    if not rows:
        return ""
    
    markdown = []
    
    # Process header row
    header_row = rows[0]
    headers = [clean_text(th.get_text()) for th in header_row.find_all(['th', 'td'])]
    if headers:
        markdown.append("| " + " | ".join(headers) + " |")
        markdown.append("| " + " | ".join(["---"] * len(headers)) + " |")
    
    # Process data rows
    for row in rows[1:]:
        cells = [clean_text(td.get_text()) for td in row.find_all(['td', 'th'])]
        if cells:
            markdown.append("| " + " | ".join(cells) + " |")
    
    return "\n".join(markdown)

def convert_html_to_markdown(html_file, output_dir):
    """Convert a single Javadoc HTML file to Markdown"""
    try:
        with open(html_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        soup = BeautifulSoup(content, 'html.parser')
        
        # Extract title
        title_tag = soup.find('title')
        title = clean_text(title_tag.get_text()) if title_tag else "Documentation"
        
        # Main content area
        content_div = soup.find('div', class_='contentContainer') or soup.find('main') or soup.find('body')
        
        if not content_div:
            return None
        
        markdown_lines = [f"# {title}\n"]
        
        # Process different sections
        for element in content_div.find_all(['h1', 'h2', 'h3', 'h4', 'p', 'pre', 'ul', 'ol', 'table', 'div']):
            if element.name in ['h1', 'h2', 'h3', 'h4']:
                level = int(element.name[1])
                text = clean_text(element.get_text())
                markdown_lines.append(f"{'#' * (level + 1)} {text}\n")
            
            elif element.name == 'p':
                text = clean_text(element.get_text())
                if text and not text.startswith("Skip navigation links"):
                    markdown_lines.append(f"{text}\n")
            
            elif element.name == 'pre':
                # Code blocks
                code_text = element.get_text()
                if code_text.strip():
                    markdown_lines.append(f"```\n{code_text}\n```\n")
            
            elif element.name in ['ul', 'ol']:
                # Lists
                list_items = element.find_all('li', recursive=False)
                for i, li in enumerate(list_items):
                    text = clean_text(li.get_text())
                    if text:
                        prefix = f"{i+1}. " if element.name == 'ol' else "- "
                        markdown_lines.append(f"{prefix}{text}")
                markdown_lines.append("")
            
            elif element.name == 'table':
                # Tables
                table_md = convert_table_to_markdown(element)
                if table_md:
                    markdown_lines.append(table_md)
                    markdown_lines.append("")
        
        # Create output file
        rel_path = os.path.relpath(html_file, 'build/docs/javadoc')
        output_path = os.path.join(output_dir, rel_path.replace('.html', '.md'))
        
        # Create directory if needed
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write("\n".join(markdown_lines))
        
        return output_path
    
    except Exception as e:
        print(f"Error converting {html_file}: {e}")
        return None

def main():
    """Main conversion function"""
    javadoc_dir = Path('build/docs/javadoc')
    output_dir = Path('docs/javadoc-markdown')
    
    if not javadoc_dir.exists():
        print("Javadoc directory not found. Run './gradlew javadoc' first.")
        return
    
    output_dir.mkdir(parents=True, exist_ok=True)
    
    # Convert key files
    key_files = [
        'index.html',
        'overview-summary.html',
        'allclasses-index.html',
        'allpackages-index.html'
    ]
    
    converted_files = []
    
    for file_name in key_files:
        html_file = javadoc_dir / file_name
        if html_file.exists():
            md_file = convert_html_to_markdown(html_file, output_dir)
            if md_file:
                converted_files.append(md_file)
                print(f"Converted: {file_name} -> {md_file}")
    
    # Convert package files
    for package_dir in javadoc_dir.iterdir():
        if package_dir.is_dir() and package_dir.name not in ['script-files', 'legal', 'resource-files']:
            for html_file in package_dir.glob('*.html'):
                md_file = convert_html_to_markdown(html_file, output_dir)
                if md_file:
                    converted_files.append(md_file)
                    print(f"Converted: {html_file.relative_to(javadoc_dir)} -> {md_file}")
    
    print(f"\nConversion complete! {len(converted_files)} files converted.")
    print(f"Markdown documentation available in: {output_dir}")
    
    # Create index
    index_path = output_dir / 'README.md'
    with open(index_path, 'w', encoding='utf-8') as f:
        f.write("# Student Planner API Documentation\n\n")
        f.write("This directory contains the Javadoc API documentation converted to Markdown format.\n\n")
        f.write("## Key Files\n\n")
        f.write("- [Overview](overview-summary.md) - Package and class overview\n")
        f.write("- [All Classes](allclasses-index.md) - Complete class index\n")
        f.write("- [All Packages](allpackages-index.md) - Package index\n\n")
        f.write("## Package Documentation\n\n")
        
        for package_dir in javadoc_dir.iterdir():
            if package_dir.is_dir() and package_dir.name not in ['script-files', 'legal', 'resource-files']:
                f.write(f"- **{package_dir.name}**\n")
                for html_file in package_dir.glob('*.html'):
                    if html_file.stem != 'package-summary':
                        md_name = html_file.relative_to(javadoc_dir).with_suffix('.md')
                        f.write(f"  - [{html_file.stem}]({md_name})\n")
    
    print(f"Created index file: {index_path}")

if __name__ == "__main__":
    main()