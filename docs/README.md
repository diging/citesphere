# Citesphere Documentation Sphinx Site

## Overview

This README file provides instructions for installing Sphinx documentation for Citesphere on various platforms, including Linux, macOS, Windows, and Docker. It also includes information on installing Sphinx from PyPI (Python Package Index) and from source. and customizing the theme of the site
Following are the steps:
1. Install Sphinx: you can find the link to Sphinx Installation here [Sphinx install](https://www.sphinx-doc.org/en/master/usage/installation.html)
2. Create a virtual environment: it is recommended to use a virtual environment for Sphinx.
3. Cutomize theme to Furo: you can find link to Furo Theme installation here [Furo](https://www.sphinx-doc.org/en/master/tutorial/more-sphinx-customization.html)
4. API Documentation: [API Documentation](https://documenter.getpostman.com/view/19365454/UVeMJiyx)

# Installing Sphinx
Sphinx is written in Python and supports Python 3.8+. It builds upon the shoulders of many third-party libraries such as Docutils and Jinja, which are installed when Sphinx is installed. Following are the steps to install Sphinx on different Operating Systems.
## Linux

### Debian/Ubuntu

Install `python3-sphinx` using apt-get:

```
apt-get install python3-sphinx
```

If Python is not already installed, this command will install it for you.

### RHEL, CentOS

Install `python-sphinx` using yum:

```
yum install python-sphinx
```

If Python is not already installed, this command will install it for you.

### Other distributions

Most Linux distributions have Sphinx in their package repositories. The package name may vary, but it is usually `python3-sphinx`, `python-sphinx`, or `sphinx`. Note that there are other packages with similar names, such as a speech recognition toolkit (CMU Sphinx) and a full-text search database (Sphinx search).

## macOS

Sphinx can be installed on macOS using Homebrew, MacPorts, or as part of a Python distribution like Anaconda.

### Homebrew

Install Sphinx using Homebrew:

```
brew install sphinx-doc
```

For more information, refer to the package overview.

### MacPorts

Install `python3x-sphinx` using MacPorts:

```
sudo port install py38-sphinx
```

To set up the executable paths, use the port select command:

```
sudo port select --set python python38
sudo port select --set sphinx py38-sphinx
```

For more information, refer to the package overview.

### Anaconda

Install Sphinx using Anaconda:

```
conda install sphinx
```

## Windows

Sphinx can be installed on Windows using Chocolatey or by manual installation.

### Chocolatey

Install Sphinx using Chocolatey:

```
choco install sphinx
```

You need to install Chocolatey before running this command. For more information, refer to the Chocolatey page.

### Other Methods

Most Windows users do not have Python installed by default. Follow these steps to install Python and then install Sphinx using pip:

1. Check if Python is already installed by opening the Command Prompt (press ⊞Win-r and type `cmd`). Once the command prompt is open, type `python --version` and press Enter. If Python is installed, you will see the version number printed. If Python is not installed, refer to the Hitchhiker's Guide to Python's Python on Windows installation guides. Make sure to install Python 3.

2. After installing Python, open the Command Prompt and run the following command to install Sphinx using pip:

```
pip install -U sphinx
```

3. To verify the installation, type `sphinx-build --version` in the command prompt. If everything is installed correctly, you will see the version number for the Sphinx package.

# Using virtual environments

When installing Sphinx using pip, it is highly recommended to use virtual environments to isolate the installed packages. This avoids the need for administrator privileges. To create a virtual environment in the `.venv` directory, use the following command:

```
python -m venv .venv
```

Note: In some Linux distributions (e.g., Debian and Ubuntu), you may need to install the `python3-venv` package before using virtual environments:

```
apt-get install python3-venv
```

## Docker

Docker images for Sphinx are available on the Docker Hub. There are two types of images:

- `sphinxdoc/sphinx`: Used for standard usage of Sphinx.
- `sphinxdoc/sphinx-latexpdf`: Primarily used for PDF builds using LaTeX
# Theme Customization

This repository contains documentation for a project and provides instructions on how to customize the appearance of the documentation using themes in Sphinx.

## Themes Overview

Themes in Sphinx allow you to personalize the visual style of your documentation. Sphinx comes with several built-in themes, and there are also third-party themes available for use.

## Installing a Third-Party Theme

To use a third-party theme like "Furo" for your HTML documentation, follow the steps below:

1. Install the theme using `pip` in your Python virtual environment. Open your terminal or command prompt and run the following command:

   ```shell
   pip install furo
   ```

2. Once the theme is installed, locate the `conf.py` file in the `docs/source` directory of your project.

3. Open the `conf.py` file in a text editor of your choice.

4. Find the `html_theme` variable in the `conf.py` file. This variable determines the theme to be used for HTML and HTML Help pages.

   ```python
   # The theme to use for HTML and HTML Help pages. See the documentation for
   # a list of builtin themes.
   #
   html_theme = 'furo'
   ```

5. Replace the value of the `html_theme` variable with the name of the third-party theme you want to use. In this case, set it to `'furo'` to use the Furo theme.

   ```python
   html_theme = 'furo'
   ```

6. Save the changes to the `conf.py` file.

7. Regenerate your documentation using Sphinx. Run the appropriate command in your terminal or command prompt, depending on your project setup.

8. After regenerating the documentation, open the HTML version, and you will notice that the documentation now has a new appearance based on the chosen theme.

## Conclusion

By following the steps outlined above, you can easily setup the sphinx documentation site for citesphere locally.
